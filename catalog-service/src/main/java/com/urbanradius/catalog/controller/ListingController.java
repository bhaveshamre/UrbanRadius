package com.urbanradius.catalog.controller;

import com.urbanradius.catalog.dto.CreateListingRequest;
import com.urbanradius.catalog.dto.ListingResponse;
import com.urbanradius.catalog.dto.UpdateListingRequest;
import com.urbanradius.catalog.enums.ListingCategory;
import com.urbanradius.catalog.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse create(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.create(request, jwt.getSubject());
    }

    @GetMapping("/my")
    public List<ListingResponse> getMyListings(@AuthenticationPrincipal Jwt jwt) {
        return listingService.getMyListings(jwt.getSubject());
    }

    @GetMapping
    public List<ListingResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ListingCategory category,
            @RequestParam(required = false) String subcategory) {
        return listingService.search(city, category, subcategory);
    }

    @GetMapping("/{id}")
    public ListingResponse getById(@PathVariable UUID id) {
        return listingService.getById(id);
    }

    @PutMapping("/{id}")
    public ListingResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateListingRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.update(id, request, jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    public ListingResponse deactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.deactivate(id, jwt.getSubject());
    }
}
