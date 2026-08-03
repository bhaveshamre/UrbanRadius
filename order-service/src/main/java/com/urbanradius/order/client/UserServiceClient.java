package com.urbanradius.order.client;

import com.urbanradius.common.dto.UserRole;
import com.urbanradius.common.exception.UrbanRadiusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder,
                             @Value("${urban-radius.user-service-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public UserProfileView getCurrentUser(String bearerToken) {
        try {
            UserProfileView profile = restClient.get()
                    .uri("/api/users/me")
                    .header("Authorization", bearerToken)
                    .retrieve()
                    .body(UserProfileView.class);

            if (profile == null) {
                throw new UrbanRadiusException(
                        "USER_SERVICE_ERROR",
                        "Empty response from user service",
                        502
                );
            }
            return profile;
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new UrbanRadiusException(
                        "PROFILE_NOT_FOUND",
                        "Register your profile before creating a booking",
                        404
                );
            }
            throw new UrbanRadiusException(
                    "USER_SERVICE_ERROR",
                    "Failed to fetch user profile",
                    502
            );
        }
    }

    public record UserProfileView(
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
    }
}
