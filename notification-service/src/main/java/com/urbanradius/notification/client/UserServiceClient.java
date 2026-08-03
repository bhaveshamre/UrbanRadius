package com.urbanradius.notification.client;

import com.urbanradius.common.dto.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder,
                             @Value("${urban-radius.user-service-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public Optional<UserProfileView> findUserById(UUID userId) {
        try {
            UserProfileView profile = restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .body(UserProfileView.class);
            return Optional.ofNullable(profile);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                log.warn("User profile not found: {}", userId);
                return Optional.empty();
            }
            log.error("Failed to fetch user profile {}: {}", userId, ex.getMessage());
            return Optional.empty();
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
