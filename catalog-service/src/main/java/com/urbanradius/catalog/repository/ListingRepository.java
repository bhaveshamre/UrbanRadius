package com.urbanradius.catalog.repository;

import com.urbanradius.catalog.enums.ListingCategory;
import com.urbanradius.catalog.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ListingRepository extends MongoRepository<Listing, UUID> {

    List<Listing> findByKeycloakIdAndActiveTrue(String keycloakId);

    List<Listing> findByActiveTrue();

    List<Listing> findByCityAndActiveTrue(String city);

    List<Listing> findByCityAndCategoryAndActiveTrue(String city, ListingCategory category);

    List<Listing> findByCityAndCategoryAndSubcategoryAndActiveTrue(
            String city,
            ListingCategory category,
            String subcategory);
}
