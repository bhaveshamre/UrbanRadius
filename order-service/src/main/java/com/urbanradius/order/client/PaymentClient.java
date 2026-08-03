package com.urbanradius.order.client;

import com.urbanradius.common.dto.PaymentStatus;
import com.urbanradius.common.exception.UrbanRadiusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(RestClient.Builder restClientBuilder,
                         @Value("${urban-radius.payment-service-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public PaymentView holdFunds(HoldPaymentRequest request, String idempotencyKey, String authorizationHeader) {
        try {
            PaymentView payment = restClient.post()
                    .uri("/api/payments/hold")
                    .header("Authorization", authorizationHeader)
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(PaymentView.class);

            if (payment == null) {
                throw new UrbanRadiusException(
                        "PAYMENT_SERVICE_ERROR",
                        "Empty response from payment service",
                        502
                );
            }
            return payment;
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 402) {
                throw new UrbanRadiusException(
                        "PAYMENT_HOLD_FAILED",
                        "Payment hold was declined",
                        402
                );
            }
            throw new UrbanRadiusException(
                    "PAYMENT_SERVICE_ERROR",
                    "Failed to hold payment for booking",
                    502
            );
        }
    }

    public PaymentView getByBookingId(UUID bookingId, String authorizationHeader) {
        try {
            PaymentView payment = restClient.get()
                    .uri("/api/payments/booking/{bookingId}", bookingId)
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .body(PaymentView.class);

            if (payment == null) {
                throw new UrbanRadiusException(
                        "PAYMENT_NOT_FOUND",
                        "No payment found for booking: " + bookingId,
                        404
                );
            }
            return payment;
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new UrbanRadiusException(
                        "PAYMENT_NOT_FOUND",
                        "No payment found for booking: " + bookingId,
                        404
                );
            }
            throw new UrbanRadiusException(
                    "PAYMENT_SERVICE_ERROR",
                    "Failed to fetch payment for booking",
                    502
            );
        }
    }

    public PaymentView releaseFunds(UUID paymentId, String authorizationHeader) {
        try {
            return postPaymentAction("/api/payments/{id}/release", paymentId, authorizationHeader);
        } catch (HttpStatusCodeException ex) {
            throw mapActionError(ex, "release");
        }
    }

    public PaymentView refundFunds(UUID paymentId, String authorizationHeader) {
        try {
            return postPaymentAction("/api/payments/{id}/refund", paymentId, authorizationHeader);
        } catch (HttpStatusCodeException ex) {
            throw mapActionError(ex, "refund");
        }
    }

    private PaymentView postPaymentAction(String uri, UUID paymentId, String authorizationHeader) {
        PaymentView payment = restClient.post()
                .uri(uri, paymentId)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(PaymentView.class);

        if (payment == null) {
            throw new UrbanRadiusException(
                    "PAYMENT_SERVICE_ERROR",
                    "Empty response from payment service",
                    502
            );
        }
        return payment;
    }

    private UrbanRadiusException mapActionError(HttpStatusCodeException ex, String action) {
        if (ex.getStatusCode().value() == 400) {
            return new UrbanRadiusException(
                    "INVALID_PAYMENT_STATUS_TRANSITION",
                    "Payment cannot be " + action + "d in its current state",
                    400
            );
        }
        return new UrbanRadiusException(
                "PAYMENT_SERVICE_ERROR",
                "Failed to " + action + " payment",
                502
        );
    }

    public record HoldPaymentRequest(
            UUID bookingId,
            UUID seekerId,
            UUID providerId,
            BigDecimal amount,
            String currency
    ) {
    }

    public record PaymentView(
            UUID id,
            UUID bookingId,
            UUID seekerId,
            UUID providerId,
            BigDecimal amount,
            String currency,
            PaymentStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
