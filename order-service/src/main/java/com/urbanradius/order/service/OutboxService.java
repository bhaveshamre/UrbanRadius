package com.urbanradius.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanradius.common.events.BookingEvent;
import com.urbanradius.common.exception.UrbanRadiusException;
import com.urbanradius.order.model.OutboxEvent;
import com.urbanradius.order.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public void enqueue(BookingEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent(
                    event.bookingId(),
                    event.eventType(),
                    payload
            ));
        } catch (JsonProcessingException ex) {
            throw new UrbanRadiusException(
                    "OUTBOX_SERIALIZATION_ERROR",
                    "Failed to serialize booking event",
                    500,
                    ex
            );
        }
    }
}
