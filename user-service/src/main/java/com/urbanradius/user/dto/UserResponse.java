package com.urbanradius.user.dto;

import com.urbanradius.common.dto.UserRole;
import com.urbanradius.user.model.UserProfile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String phone,
        String city,
        UserRole role,
        BigDecimal averageRating,
        int ratingCount,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserResponse from(UserProfile profile) {
        return new UserResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getFullName(),
                profile.getPhone(),
                profile.getCity(),
                profile.getRole(),
                profile.getAverageRating(),
                profile.getRatingCount(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
