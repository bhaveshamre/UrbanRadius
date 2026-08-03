package com.urbanradius.payment.controller;

import com.urbanradius.payment.dto.HoldPaymentRequest;
import com.urbanradius.payment.dto.PaymentResponse;
import com.urbanradius.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/hold")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse holdFunds(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody HoldPaymentRequest request,
            HttpServletRequest httpRequest) {
        return paymentService.holdFunds(request, idempotencyKey, httpRequest.getHeader("Authorization"));
    }

    @GetMapping("/booking/{bookingId}")
    public PaymentResponse getByBookingId(
            @PathVariable UUID bookingId,
            HttpServletRequest httpRequest) {
        return paymentService.getByBookingId(bookingId, httpRequest.getHeader("Authorization"));
    }

    @PostMapping("/{id}/release")
    public PaymentResponse releaseFunds(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return paymentService.releaseFunds(id, httpRequest.getHeader("Authorization"));
    }

    @PostMapping("/{id}/refund")
    public PaymentResponse refundFunds(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        return paymentService.refundFunds(id, httpRequest.getHeader("Authorization"));
    }
}
