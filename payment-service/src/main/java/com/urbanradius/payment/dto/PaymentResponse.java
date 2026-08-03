package com.urbanradius.payment.dto;

import com.urbanradius.common.dto.PaymentStatus;
import com.urbanradius.payment.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID bookingId,
        UUID seekerId,
        UUID providerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getSeekerId(),
                payment.getProviderId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
