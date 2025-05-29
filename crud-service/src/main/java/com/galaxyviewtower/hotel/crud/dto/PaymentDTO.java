package com.galaxyviewtower.hotel.crud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String id;
    private String bookingId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String paymentGateway;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 