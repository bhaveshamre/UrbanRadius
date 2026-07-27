package com.urbanradius.catalog.service;

import com.urbanradius.common.exception.UrbanRadiusException;
import com.urbanradius.catalog.dto.CreateListingRequest;
import com.urbanradius.catalog.dto.ListingResponse;
import com.urbanradius.catalog.dto.UpdateListingRequest;
import com.urbanradius.catalog.enums.ListingCategory;
import com.urbanradius.catalog.model.Listing;
import com.urbanradius.catalog.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public ListingResponse create(CreateListingRequest request, String keycloakId) {
        Listing listing = new Listing(
                request.providerId(),
                keycloakId,
                request.title(),
                request.description(),
                request.category(),
                request.subcategory(),
                request.priceAmount(),
                request.priceUnit(),
                request.city(),
                request.attributes()
        );

        return ListingResponse.from(listingRepository.save(listing));
    }

    public ListingResponse getById(UUID id) {
        return ListingResponse.from(findActiveListingOrThrow(id));
    }

    public List<ListingResponse> search(String city, ListingCategory category, String subcategory) {
        List<Listing> listings;

        if (city != null && category != null && subcategory != null) {
            listings = listingRepository.findByCityAndCategoryAndSubcategoryAndActiveTrue(
                    city, category, subcategory);
        } else if (city != null && category != null) {
            listings = listingRepository.findByCityAndCategoryAndActiveTrue(city, category);
        } else if (city != null) {
            listings = listingRepository.findByCityAndActiveTrue(city);
        } else {
            listings = listingRepository.findByActiveTrue();
        }

        return listings.stream().map(ListingResponse::from).toList();
    }

    public List<ListingResponse> getMyListings(String keycloakId) {
        return listingRepository.findByKeycloakIdAndActiveTrue(keycloakId).stream()
                .map(ListingResponse::from)
                .toList();
    }

    public ListingResponse update(UUID id, UpdateListingRequest request, String keycloakId) {
        Listing listing = findListingOrThrow(id);
        verifyOwnership(listing, keycloakId);

        listing.update(
                request.title(),
                request.description(),
                request.category(),
                request.subcategory(),
                request.priceAmount(),
                request.priceUnit(),
                request.city(),
                request.attributes()
        );

        return ListingResponse.from(listingRepository.save(listing));
    }

    public ListingResponse deactivate(UUID id, String keycloakId) {
        Listing listing = findListingOrThrow(id);
        verifyOwnership(listing, keycloakId);

        listing.deactivate();
        return ListingResponse.from(listingRepository.save(listing));
    }

    private Listing findActiveListingOrThrow(UUID id) {
        Listing listing = findListingOrThrow(id);
        if (!listing.isActive()) {
            throw new UrbanRadiusException(
                    "LISTING_NOT_FOUND",
                    "Listing not found: " + id,
                    404
            );
        }
        return listing;
    }

    private Listing findListingOrThrow(UUID id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new UrbanRadiusException(
                        "LISTING_NOT_FOUND",
                        "Listing not found: " + id,
                        404
                ));
    }

    private void verifyOwnership(Listing listing, String keycloakId) {
        if (!listing.getKeycloakId().equals(keycloakId)) {
            throw new UrbanRadiusException(
                    "FORBIDDEN",
                    "You can only modify your own listings",
                    403
            );
        }
    }
}
