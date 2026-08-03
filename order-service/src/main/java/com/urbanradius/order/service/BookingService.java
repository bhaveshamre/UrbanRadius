package com.urbanradius.order.service;

import com.urbanradius.common.dto.BookingStatus;
import com.urbanradius.common.dto.UserRole;
import com.urbanradius.common.events.BookingEvent;
import com.urbanradius.common.exception.UrbanRadiusException;
import com.urbanradius.order.client.CatalogClient;
import com.urbanradius.order.client.PaymentClient;
import com.urbanradius.order.client.UserServiceClient;
import com.urbanradius.order.dto.BookingResponse;
import com.urbanradius.order.dto.CreateBookingRequest;
import com.urbanradius.order.model.Booking;
import com.urbanradius.order.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private static final String DEFAULT_CURRENCY = "INR";
    private static final String BOOKING_CREATED = "booking.created";
    private static final String BOOKING_ACCEPTED = "booking.accepted";
    private static final String BOOKING_STARTED = "booking.started";
    private static final String BOOKING_COMPLETED = "booking.completed";
    private static final String BOOKING_CANCELLED = "booking.cancelled";

    private final BookingRepository bookingRepository;
    private final CatalogClient catalogClient;
    private final UserServiceClient userServiceClient;
    private final PaymentClient paymentClient;
    private final OutboxService outboxService;

    public BookingService(
            BookingRepository bookingRepository,
            CatalogClient catalogClient,
            UserServiceClient userServiceClient,
            PaymentClient paymentClient,
            OutboxService outboxService) {
        this.bookingRepository = bookingRepository;
        this.catalogClient = catalogClient;
        this.userServiceClient = userServiceClient;
        this.paymentClient = paymentClient;
        this.outboxService = outboxService;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, String authorizationHeader) {
        UserServiceClient.UserProfileView seeker = userServiceClient.getCurrentUser(authorizationHeader);

        if (seeker.role() != UserRole.SEEKER) {
            throw new UrbanRadiusException(
                    "SEEKER_ROLE_REQUIRED",
                    "Only seekers can create bookings",
                    403
            );
        }

        CatalogClient.ListingView listing = catalogClient.getListing(request.listingId());

        if (!listing.active()) {
            throw new UrbanRadiusException(
                    "LISTING_INACTIVE",
                    "Listing is not available for booking",
                    400
            );
        }

        Booking booking = new Booking(
                request.listingId(),
                seeker.id(),
                listing.providerId(),
                request.scheduledAt(),
                request.notes()
        );

        Booking saved = bookingRepository.save(booking);

        paymentClient.holdFunds(
                new PaymentClient.HoldPaymentRequest(
                        saved.getId(),
                        saved.getSeekerId(),
                        saved.getProviderId(),
                        listing.priceAmount(),
                        DEFAULT_CURRENCY
                ),
                saved.getId().toString(),
                authorizationHeader
        );

        publishEvent(saved, BOOKING_CREATED);

        return BookingResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID bookingId, String authorizationHeader) {
        UserServiceClient.UserProfileView user = userServiceClient.getCurrentUser(authorizationHeader);
        Booking booking = findBookingOrThrow(bookingId);
        assertParticipant(booking, user.id());
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String authorizationHeader) {
        UserServiceClient.UserProfileView user = userServiceClient.getCurrentUser(authorizationHeader);
        return bookingRepository.findAllForParticipant(user.id()).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse acceptBooking(UUID bookingId, String authorizationHeader) {
        UserServiceClient.UserProfileView user = userServiceClient.getCurrentUser(authorizationHeader);
        Booking booking = findBookingOrThrow(bookingId);
        assertProvider(booking, user.id());
        transition(booking, BookingStatus.ACCEPTED, BOOKING_ACCEPTED);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse startBooking(UUID bookingId, String authorizationHeader) {
        UserServiceClient.UserProfileView user = userServiceClient.getCurrentUser(authorizationHeader);
        Booking booking = findBookingOrThrow(bookingId);
        assertProvider(booking, user.id());
        transition(booking, BookingStatus.IN_PROGRESS, BOOKING_STARTED);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse completeBooking(UUID bookingId, String authorizationHeader) {
        UserServiceClient.UserProfileView user = userServiceClient.getCurrentUser(authorizationHeader);
        Booking booking = findBookingOrThrow(bookingId);
        assertProvider(booking, user.id());
        assertTransitionAllowed(booking, BookingStatus.COMPLETED);

        PaymentClient.PaymentView payment = paymentClient.getByBookingId(bookingId, authorizationHeader);
        paymentClient.releaseFunds(payment.id(), authorizationHeader);

        transition(booking, BookingStatus.COMPLETED, BOOKING_COMPLETED);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(UUID bookingId, String authorizationHeader) {
        UserServiceClient.UserProfileView user = userServiceClient.getCurrentUser(authorizationHeader);
        Booking booking = findBookingOrThrow(bookingId);
        assertParticipant(booking, user.id());
        assertTransitionAllowed(booking, BookingStatus.CANCELLED);

        PaymentClient.PaymentView payment = paymentClient.getByBookingId(bookingId, authorizationHeader);
        paymentClient.refundFunds(payment.id(), authorizationHeader);

        transition(booking, BookingStatus.CANCELLED, BOOKING_CANCELLED);
        return BookingResponse.from(booking);
    }

    private Booking findBookingOrThrow(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UrbanRadiusException(
                        "BOOKING_NOT_FOUND",
                        "Booking not found: " + bookingId,
                        404
                ));
    }

    private void assertParticipant(Booking booking, UUID userId) {
        if (!booking.getSeekerId().equals(userId) && !booking.getProviderId().equals(userId)) {
            throw new UrbanRadiusException(
                    "BOOKING_ACCESS_DENIED",
                    "You are not a participant on this booking",
                    403
            );
        }
    }

    private void assertProvider(Booking booking, UUID userId) {
        if (!booking.getProviderId().equals(userId)) {
            throw new UrbanRadiusException(
                    "PROVIDER_ACCESS_REQUIRED",
                    "Only the provider on this booking can perform this action",
                    403
            );
        }
    }

    private void assertTransitionAllowed(Booking booking, BookingStatus targetStatus) {
        if (!isAllowedTransition(booking.getStatus(), targetStatus)) {
            throw new UrbanRadiusException(
                    "INVALID_STATUS_TRANSITION",
                    "Cannot transition from " + booking.getStatus() + " to " + targetStatus,
                    400
            );
        }
    }

    private void transition(Booking booking, BookingStatus targetStatus, String eventType) {
        assertTransitionAllowed(booking, targetStatus);

        booking.transitionTo(targetStatus);
        bookingRepository.save(booking);
        publishEvent(booking, eventType);
    }

    private void publishEvent(Booking booking, String eventType) {
        outboxService.enqueue(BookingEvent.of(
                booking.getId(),
                booking.getSeekerId(),
                booking.getProviderId(),
                booking.getStatus(),
                eventType
        ));
    }

    private boolean isAllowedTransition(BookingStatus currentStatus, BookingStatus targetStatus) {
        return switch (currentStatus) {
            case REQUESTED -> targetStatus == BookingStatus.ACCEPTED
                    || targetStatus == BookingStatus.CANCELLED;
            case ACCEPTED -> targetStatus == BookingStatus.IN_PROGRESS
                    || targetStatus == BookingStatus.CANCELLED;
            case IN_PROGRESS -> targetStatus == BookingStatus.COMPLETED
                    || targetStatus == BookingStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
