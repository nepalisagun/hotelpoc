package com.galaxyviewtower.hotel.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request object for initiating a payment")
public class PaymentRequest {

    @Schema(description = "ID of the booking to be paid", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @Schema(description = "ID of the user making the payment", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Amount to be paid", example = "299.99")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    @NotBlank(message = "Currency is required")
    private String currency;

    @Schema(description = "Payment gateway to use", example = "STRIPE")
    @NotBlank(message = "Payment gateway is required")
    private String paymentGateway;

    @Schema(description = "Tokenized payment method details", example = "tok_visa_123")
    @NotBlank(message = "Payment method details are required")
    private String paymentMethodDetailsTokenized;

    @Schema(description = "Unique key to ensure idempotency of payment requests", example = "payment_123456789")
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
} 