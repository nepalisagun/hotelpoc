package com.galaxyviewtower.hotel.crud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String id;
    private String userId;
    private String roomId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private BigDecimal totalAmount;
    private String status;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 