package com.urbanradius.order.client;

import com.urbanradius.common.exception.UrbanRadiusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(RestClient.Builder restClientBuilder,
                         @Value("${urban-radius.catalog-service-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public ListingView getListing(UUID listingId) {
        try {
            return restClient.get()
                    .uri("/api/listings/{id}", listingId)
                    .retrieve()
                    .body(ListingView.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new UrbanRadiusException(
                        "LISTING_NOT_FOUND",
                        "Listing not found: " + listingId,
                        404
                );
            }
            throw new UrbanRadiusException(
                    "CATALOG_SERVICE_ERROR",
                    "Failed to fetch listing from catalog service",
                    502
            );
        }
    }

    public record ListingView(
            UUID id,
            UUID providerId,
            String title,
            BigDecimal priceAmount,
            String city,
            boolean active,
            Map<String, Object> attributes,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
