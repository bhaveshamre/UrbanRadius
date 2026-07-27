package com.urbanradius.catalog.model;

import com.urbanradius.catalog.enums.ListingCategory;
import com.urbanradius.catalog.enums.PriceUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Document(collection = "listings")
public class Listing {

    @Id
    private UUID id;

    @Indexed
    private UUID providerId;

    @Indexed
    private String keycloakId;

    private String title;

    private String description;

    @Indexed
    private ListingCategory category;

    private String subcategory;

    private BigDecimal priceAmount;

    private PriceUnit priceUnit;

    @Indexed
    private String city;

    private boolean active;

    private Map<String, Object> attributes;

    private Instant createdAt;

    private Instant updatedAt;

    protected Listing() {
    }

    public Listing(
            UUID providerId,
            String keycloakId,
            String title,
            String description,
            ListingCategory category,
            String subcategory,
            BigDecimal priceAmount,
            PriceUnit priceUnit,
            String city,
            Map<String, Object> attributes) {
        this.id = UUID.randomUUID();
        this.providerId = providerId;
        this.keycloakId = keycloakId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.priceAmount = priceAmount;
        this.priceUnit = priceUnit;
        this.city = city;
        this.active = true;
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ListingCategory getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public PriceUnit getPriceUnit() {
        return priceUnit;
    }

    public String getCity() {
        return city;
    }

    public boolean isActive() {
        return active;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(
            String title,
            String description,
            ListingCategory category,
            String subcategory,
            BigDecimal priceAmount,
            PriceUnit priceUnit,
            String city,
            Map<String, Object> attributes) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.priceAmount = priceAmount;
        this.priceUnit = priceUnit;
        this.city = city;
        this.attributes = attributes != null ? new HashMap<>(attributes) : this.attributes;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
