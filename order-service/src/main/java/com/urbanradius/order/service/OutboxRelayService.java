package com.urbanradius.order.service;

import com.urbanradius.order.model.OutboxEvent;
import com.urbanradius.order.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OutboxRelayService {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayService.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String bookingEventsTopic;

    public OutboxRelayService(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${urban-radius.kafka.booking-events-topic}") String bookingEventsTopic) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.bookingEventsTopic = bookingEventsTopic;
    }

    @Scheduled(fixedDelayString = "${urban-radius.outbox.relay-interval-ms}")
    @Transactional
    public void relayPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findTop50ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(
                        bookingEventsTopic,
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get();
                event.markPublished();
                outboxEventRepository.save(event);
                log.info("Published outbox event {} ({})", event.getId(), event.getEventType());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.warn("Outbox relay interrupted for event {}", event.getId());
                return;
            } catch (ExecutionException ex) {
                log.error("Failed to publish outbox event {}: {}", event.getId(), ex.getMessage());
            }
        }
    }
}
