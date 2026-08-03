package com.urbanradius.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record HoldPaymentRequest(
        @NotNull UUID bookingId,
        @NotNull UUID seekerId,
        @NotNull UUID providerId,
        @NotNull @DecimalMin(value = "0.01", message = "must be at least 0.01") BigDecimal amount,
        @NotBlank String currency
) {
}
