package com.galaxyviewtower.hotel.crud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private String bookingId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String paymentGateway;
    private String idempotencyKey;
} 