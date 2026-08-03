package com.urbanradius.notification.repository;

import com.urbanradius.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    boolean existsByEventIdAndRecipientId(UUID eventId, UUID recipientId);
}
