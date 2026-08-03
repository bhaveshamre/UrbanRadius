package com.urbanradius.order.dto;

import com.urbanradius.common.dto.BookingStatus;
import com.urbanradius.order.model.Booking;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID listingId,
        UUID seekerId,
        UUID providerId,
        BookingStatus status,
        Instant scheduledAt,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getListingId(),
                booking.getSeekerId(),
                booking.getProviderId(),
                booking.getStatus(),
                booking.getScheduledAt(),
                booking.getNotes(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}
