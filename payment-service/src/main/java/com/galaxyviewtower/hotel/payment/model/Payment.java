package com.galaxyviewtower.hotel.payment.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("payments")
public class Payment {

    @Id
    private String id;

    private String bookingId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String paymentGatewayTransactionId;
    private PaymentStatus status;
    private String paymentMethodDetailsTokenized;
    private String paymentGateway; // STRIPE, PAYPAL, etc.
    private String idempotencyKey;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        REFUNDED
    }
} 