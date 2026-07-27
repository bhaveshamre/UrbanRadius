package com.urbanradius.common.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.urbanradius.common.dto.BookingStatus;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingEvent(
        UUID eventId,
        UUID bookingId,
        UUID seekerId,
        UUID providerId,
        BookingStatus status,
        String eventType,
        Instant occurredAt
) {

    public static BookingEvent of(
            UUID bookingId,
            UUID seekerId,
            UUID providerId,
            BookingStatus status,
            String eventType) {
        return new BookingEvent(
                UUID.randomUUID(),
                bookingId,
                seekerId,
                providerId,
                status,
                eventType,
                Instant.now()
        );
    }
}
