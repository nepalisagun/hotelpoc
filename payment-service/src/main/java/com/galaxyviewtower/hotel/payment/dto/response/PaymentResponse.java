package com.galaxyviewtower.hotel.payment.dto.response;

import com.galaxyviewtower.hotel.payment.model.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Response object containing payment information")
public class PaymentResponse {

    @Schema(description = "Unique identifier of the payment", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "ID of the booking", example = "123e4567-e89b-12d3-a456-426614174000")
    private String bookingId;

    @Schema(description = "ID of the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private String userId;

    @Schema(description = "Payment amount", example = "299.99")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Payment gateway transaction ID", example = "txn_123456789")
    private String paymentGatewayTransactionId;

    @Schema(description = "Payment status", example = "SUCCESS")
    private Payment.PaymentStatus status;

    @Schema(description = "Payment gateway used", example = "STRIPE")
    private String paymentGateway;

    @Schema(description = "Timestamp when the payment was created", example = "2024-03-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the payment was last updated", example = "2024-03-15T10:30:00")
    private LocalDateTime updatedAt;

    public static PaymentResponse fromPayment(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setBookingId(payment.getBookingId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentGatewayTransactionId(payment.getPaymentGatewayTransactionId());
        response.setStatus(payment.getStatus());
        response.setPaymentGateway(payment.getPaymentGateway());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }
} 