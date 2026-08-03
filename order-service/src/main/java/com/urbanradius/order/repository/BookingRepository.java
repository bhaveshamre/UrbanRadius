package com.urbanradius.order.repository;

import com.urbanradius.order.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
            SELECT b FROM Booking b
            WHERE b.seekerId = :userId OR b.providerId = :userId
            ORDER BY b.createdAt DESC
            """)
    List<Booking> findAllForParticipant(@Param("userId") UUID userId);
}
