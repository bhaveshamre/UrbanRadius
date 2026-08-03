package com.urbanradius.notification.consumer;

import com.urbanradius.common.events.BookingEvent;
import com.urbanradius.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventConsumer {

    private final NotificationService notificationService;

    public BookingEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${urban-radius.kafka.booking-events-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "bookingEventKafkaListenerContainerFactory"
    )
    public void consume(BookingEvent event) {
        notificationService.handleBookingEvent(event);
    }
}
