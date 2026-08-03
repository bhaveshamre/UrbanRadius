package com.urbanradius.payment.service;

import com.urbanradius.common.dto.PaymentStatus;
import com.urbanradius.common.exception.UrbanRadiusException;
import com.urbanradius.payment.client.UserServiceClient;
import com.urbanradius.payment.dto.HoldPaymentRequest;
import com.urbanradius.payment.dto.PaymentResponse;
import com.urbanradius.payment.model.Payment;
import com.urbanradius.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserServiceClient userServiceClient;

    public PaymentService(PaymentRepository paymentRepository, UserServiceClient userServiceClient) {
        this.paymentRepository = paymentRepository;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public PaymentResponse holdFunds(HoldPaymentRequest request, String idempotencyKey, String authorizationHeader) {
        UserServiceClient.UserProfileView currentUser = userServiceClient.getCurrentUser(authorizationHeader);

        if (!currentUser.id().equals(request.seekerId())) {
            throw new UrbanRadiusException(
                    "SEEKER_MISMATCH",
                    "Only the seeker can authorize payment hold for this booking",
                    403
            );
        }

        return paymentRepository.findByIdempotencyKey(idempotencyKey)
                .map(PaymentResponse::from)
                .orElseGet(() -> paymentRepository.findByBookingId(request.bookingId())
                        .map(PaymentResponse::from)
                        .orElseGet(() -> createHold(request, idempotencyKey)));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByBookingId(UUID bookingId, String authorizationHeader) {
        UserServiceClient.UserProfileView currentUser = userServiceClient.getCurrentUser(authorizationHeader);
        Payment payment = findByBookingIdOrThrow(bookingId);
        assertParticipant(payment, currentUser.id());
        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse releaseFunds(UUID paymentId, String authorizationHeader) {
        UserServiceClient.UserProfileView currentUser = userServiceClient.getCurrentUser(authorizationHeader);
        Payment payment = findPaymentOrThrow(paymentId);
        assertProvider(payment, currentUser.id());
        transition(payment, PaymentStatus.RELEASED);
        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse refundFunds(UUID paymentId, String authorizationHeader) {
        UserServiceClient.UserProfileView currentUser = userServiceClient.getCurrentUser(authorizationHeader);
        Payment payment = findPaymentOrThrow(paymentId);
        assertParticipant(payment, currentUser.id());
        transition(payment, PaymentStatus.REFUNDED);
        return PaymentResponse.from(payment);
    }

    private PaymentResponse createHold(HoldPaymentRequest request, String idempotencyKey) {
        if (simulatedHoldFailure(request.amount())) {
            throw new UrbanRadiusException(
                    "PAYMENT_HOLD_FAILED",
                    "Mock gateway declined hold for amount ending in .99",
                    402
            );
        }

        Payment payment = paymentRepository.save(new Payment(
                request.bookingId(),
                request.seekerId(),
                request.providerId(),
                request.amount(),
                request.currency(),
                PaymentStatus.HELD,
                idempotencyKey
        ));
        return PaymentResponse.from(payment);
    }

    private boolean simulatedHoldFailure(BigDecimal amount) {
        BigDecimal fractional = amount.remainder(BigDecimal.ONE).abs();
        return fractional.compareTo(new BigDecimal("0.99")) == 0;
    }

    private Payment findPaymentOrThrow(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new UrbanRadiusException(
                        "PAYMENT_NOT_FOUND",
                        "Payment not found: " + paymentId,
                        404
                ));
    }

    private Payment findByBookingIdOrThrow(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new UrbanRadiusException(
                        "PAYMENT_NOT_FOUND",
                        "No payment found for booking: " + bookingId,
                        404
                ));
    }

    private void assertParticipant(Payment payment, UUID userId) {
        if (!payment.getSeekerId().equals(userId) && !payment.getProviderId().equals(userId)) {
            throw new UrbanRadiusException(
                    "PAYMENT_ACCESS_DENIED",
                    "You are not a participant on this payment",
                    403
            );
        }
    }

    private void assertProvider(Payment payment, UUID userId) {
        if (!payment.getProviderId().equals(userId)) {
            throw new UrbanRadiusException(
                    "PROVIDER_ACCESS_REQUIRED",
                    "Only the provider can release payment",
                    403
            );
        }
    }

    private void transition(Payment payment, PaymentStatus targetStatus) {
        if (payment.getStatus() != PaymentStatus.HELD) {
            throw new UrbanRadiusException(
                    "INVALID_PAYMENT_STATUS_TRANSITION",
                    "Cannot transition from " + payment.getStatus() + " to " + targetStatus,
                    400
            );
        }

        payment.transitionTo(targetStatus);
        paymentRepository.save(payment);
    }
}
