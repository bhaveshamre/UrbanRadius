package com.urbanradius.catalog.dto;

import com.urbanradius.catalog.enums.ListingCategory;
import com.urbanradius.catalog.enums.PriceUnit;
import com.urbanradius.catalog.model.Listing;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ListingResponse(
        UUID id,
        UUID providerId,
        String title,
        String description,
        ListingCategory category,
        String subcategory,
        BigDecimal priceAmount,
        PriceUnit priceUnit,
        String city,
        boolean active,
        Map<String, Object> attributes,
        Instant createdAt,
        Instant updatedAt
) {

    public static ListingResponse from(Listing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getProviderId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getCategory(),
                listing.getSubcategory(),
                listing.getPriceAmount(),
                listing.getPriceUnit(),
                listing.getCity(),
                listing.isActive(),
                listing.getAttributes(),
                listing.getCreatedAt(),
                listing.getUpdatedAt()
        );
    }
}
