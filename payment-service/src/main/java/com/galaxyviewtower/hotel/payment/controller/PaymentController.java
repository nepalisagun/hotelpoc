package com.galaxyviewtower.hotel.payment.controller;

import com.galaxyviewtower.hotel.payment.dto.request.PaymentRequest;
import com.galaxyviewtower.hotel.payment.dto.response.PaymentResponse;
import com.galaxyviewtower.hotel.payment.service.PaymentService;
import com.stripe.model.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final WebhookSecurityConfig webhookSecurityConfig;

    @PostMapping
    @Operation(
        summary = "Initiate a payment",
        description = "Creates a new payment for a booking. Supports idempotency through Idempotency-Key header."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Payment with same idempotency key already exists")
    })
    public Mono<ResponseEntity<PaymentResponse>> initiatePayment(
            @RequestBody PaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        if (idempotencyKey != null) {
            request.setIdempotencyKey(idempotencyKey);
        }
        return paymentService.initiatePayment(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{paymentId}/callback")
    @Operation(
        summary = "Process payment callback",
        description = "Handles payment status updates from payment gateways. Requires valid webhook signature."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Callback processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid webhook signature"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public Mono<ResponseEntity<PaymentResponse>> processCallback(
            @PathVariable String paymentId,
            @RequestParam String status,
            @RequestHeader("Stripe-Signature") String signature,
            @RequestBody String payload) {
        
        // Validate webhook signature
        Event event = webhookSecurityConfig.constructEvent(payload, signature);
        
        return paymentService.processPaymentCallback(paymentId, status)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(
        summary = "Refund a payment",
        description = "Processes a refund for a completed payment"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid refund request"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public Mono<ResponseEntity<PaymentResponse>> refundPayment(
            @PathVariable String paymentId) {
        return paymentService.refundPayment(paymentId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{paymentId}")
    @Operation(
        summary = "Get payment by ID",
        description = "Retrieves payment details by payment ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public Mono<ResponseEntity<PaymentResponse>> getPaymentById(
            @PathVariable String paymentId) {
        return paymentService.getPaymentById(paymentId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(
        summary = "Get payment by booking ID",
        description = "Retrieves payment details by booking ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public Mono<ResponseEntity<PaymentResponse>> getPaymentByBookingId(
            @PathVariable String bookingId) {
        return paymentService.getPaymentByBookingId(bookingId)
                .map(ResponseEntity::ok);
    }
} 