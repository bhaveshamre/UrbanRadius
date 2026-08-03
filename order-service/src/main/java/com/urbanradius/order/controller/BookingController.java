package com.urbanradius.order.controller;

import com.urbanradius.order.dto.BookingResponse;
import com.urbanradius.order.dto.CreateBookingRequest;
import com.urbanradius.order.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            HttpServletRequest httpRequest) {
        return bookingService.createBooking(request, httpRequest.getHeader("Authorization"));
    }

    @GetMapping("/my")
    public List<BookingResponse> getMyBookings(HttpServletRequest httpRequest) {
        return bookingService.getMyBookings(httpRequest.getHeader("Authorization"));
    }

    @GetMapping("/{id}")
    public BookingResponse getBooking(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return bookingService.getBooking(id, httpRequest.getHeader("Authorization"));
    }

    @PatchMapping("/{id}/accept")
    public BookingResponse acceptBooking(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return bookingService.acceptBooking(id, httpRequest.getHeader("Authorization"));
    }

    @PatchMapping("/{id}/start")
    public BookingResponse startBooking(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return bookingService.startBooking(id, httpRequest.getHeader("Authorization"));
    }

    @PatchMapping("/{id}/complete")
    public BookingResponse completeBooking(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return bookingService.completeBooking(id, httpRequest.getHeader("Authorization"));
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return bookingService.cancelBooking(id, httpRequest.getHeader("Authorization"));
    }
}
