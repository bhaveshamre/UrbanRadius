package com.urbanradius.order.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID listingId,
        @NotNull Instant scheduledAt,
        String notes
) {
}
