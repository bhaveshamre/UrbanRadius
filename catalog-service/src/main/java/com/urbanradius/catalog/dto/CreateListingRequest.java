package com.urbanradius.catalog.dto;

import com.urbanradius.catalog.enums.ListingCategory;
import com.urbanradius.catalog.enums.PriceUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CreateListingRequest(
        @NotNull UUID providerId,
        @NotBlank String title,
        @NotBlank String description,
        @NotNull ListingCategory category,
        @NotBlank String subcategory,
        @NotNull @DecimalMin("0.01") BigDecimal priceAmount,
        @NotNull PriceUnit priceUnit,
        @NotBlank String city,
        Map<String, Object> attributes
) {
}
