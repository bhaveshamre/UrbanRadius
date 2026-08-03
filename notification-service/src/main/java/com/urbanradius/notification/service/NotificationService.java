package com.urbanradius.notification.service;

import com.urbanradius.common.events.BookingEvent;
import com.urbanradius.notification.client.UserServiceClient;
import com.urbanradius.notification.model.Notification;
import com.urbanradius.notification.model.NotificationChannel;
import com.urbanradius.notification.model.NotificationStatus;
import com.urbanradius.notification.repository.NotificationRepository;
import com.urbanradius.notification.sender.MockNotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private static final String BOOKING_CREATED = "booking.created";
    private static final String BOOKING_ACCEPTED = "booking.accepted";
    private static final String BOOKING_STARTED = "booking.started";
    private static final String BOOKING_COMPLETED = "booking.completed";
    private static final String BOOKING_CANCELLED = "booking.cancelled";

    private final UserServiceClient userServiceClient;
    private final MockNotificationSender notificationSender;
    private final NotificationRepository notificationRepository;

    public NotificationService(
            UserServiceClient userServiceClient,
            MockNotificationSender notificationSender,
            NotificationRepository notificationRepository) {
        this.userServiceClient = userServiceClient;
        this.notificationSender = notificationSender;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void handleBookingEvent(BookingEvent event) {
        log.info(
                "Processing booking event: eventId={} bookingId={} eventType={}",
                event.eventId(),
                event.bookingId(),
                event.eventType()
        );

        switch (event.eventType()) {
            case BOOKING_CREATED -> notifyUser(
                    event,
                    event.providerId(),
                    formatCreatedMessage(event)
            );
            case BOOKING_ACCEPTED -> notifyUser(
                    event,
                    event.seekerId(),
                    formatAcceptedMessage(event)
            );
            case BOOKING_STARTED -> notifyUser(
                    event,
                    event.seekerId(),
                    formatStartedMessage(event)
            );
            case BOOKING_COMPLETED -> {
                notifyUser(event, event.seekerId(), formatCompletedMessageForSeeker(event));
                notifyUser(event, event.providerId(), formatCompletedMessageForProvider(event));
            }
            case BOOKING_CANCELLED -> {
                notifyUser(event, event.seekerId(), formatCancelledMessage(event));
                notifyUser(event, event.providerId(), formatCancelledMessage(event));
            }
            default -> log.warn("Unhandled booking event type: {}", event.eventType());
        }
    }

    private void notifyUser(BookingEvent event, UUID recipientId, String message) {
        if (notificationRepository.existsByEventIdAndRecipientId(event.eventId(), recipientId)) {
            log.info(
                    "Skipping duplicate notification: eventId={} recipientId={}",
                    event.eventId(),
                    recipientId
            );
            return;
        }

        userServiceClient.findUserById(recipientId).ifPresentOrElse(
                recipient -> {
                    notificationSender.sendEmail(recipient, message);
                    notificationRepository.save(new Notification(
                            event.eventId(),
                            event.bookingId(),
                            recipientId,
                            NotificationChannel.EMAIL,
                            event.eventType(),
                            message,
                            NotificationStatus.SENT
                    ));
                },
                () -> log.warn(
                        "Skipping notification — recipient not found: eventId={} recipientId={}",
                        event.eventId(),
                        recipientId
                )
        );
    }

    private String formatCreatedMessage(BookingEvent event) {
        return "You have a new booking request for booking "
                + event.bookingId()
                + ". Please review and accept or decline.";
    }

    private String formatAcceptedMessage(BookingEvent event) {
        return "Your booking "
                + event.bookingId()
                + " was accepted by the provider. They will start at the scheduled time.";
    }

    private String formatStartedMessage(BookingEvent event) {
        return "Your booking "
                + event.bookingId()
                + " is now in progress. The provider has started the service.";
    }

    private String formatCompletedMessageForSeeker(BookingEvent event) {
        return "Your booking "
                + event.bookingId()
                + " is complete. Thank you for using Urban Radius — please rate your provider.";
    }

    private String formatCompletedMessageForProvider(BookingEvent event) {
        return "Booking "
                + event.bookingId()
                + " is marked complete. Payment will be released to your account.";
    }

    private String formatCancelledMessage(BookingEvent event) {
        return "Booking "
                + event.bookingId()
                + " was cancelled. Any held payment will be refunded to the seeker.";
    }
}
