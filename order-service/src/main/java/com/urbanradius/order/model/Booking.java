package com.urbanradius.order.model;

import com.urbanradius.common.dto.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private UUID id;

    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "seeker_id", nullable = false)
    private UUID seekerId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Booking() {
    }

    public Booking(
            UUID listingId,
            UUID seekerId,
            UUID providerId,
            Instant scheduledAt,
            String notes) {
        this.id = UUID.randomUUID();
        this.listingId = listingId;
        this.seekerId = seekerId;
        this.providerId = providerId;
        this.status = BookingStatus.REQUESTED;
        this.scheduledAt = scheduledAt;
        this.notes = notes;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public UUID getSeekerId() {
        return seekerId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void transitionTo(BookingStatus newStatus) {
        this.status = newStatus;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
