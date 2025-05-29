package com.galaxyviewtower.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Response object containing booking details")
public class BookingResponseDto {
    @Schema(description = "Unique identifier of the booking", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "ID of the booked hotel", example = "123e4567-e89b-12d3-a456-426614174001")
    private String hotelId;

    @Schema(description = "ID of the user who made the booking", example = "123e4567-e89b-12d3-a456-426614174002")
    private String userId;
    
    @Schema(description = "Check-in date", example = "2024-06-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;
    
    @Schema(description = "Check-out date", example = "2024-06-05")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    
    @Schema(description = "Number of rooms booked", example = "2")
    private Integer rooms;

    @Schema(description = "Total price for the booking", example = "599.98")
    private BigDecimal totalPrice;

    @Schema(description = "Current status of the booking", example = "CONFIRMED")
    private BookingStatus status;

    @Schema(description = "Special requests or notes for the booking", example = "Please provide a room with ocean view")
    private String specialRequests;
    
    @Schema(description = "Timestamp when the booking was created", example = "2024-05-29T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the booking was last updated", example = "2024-05-29T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Possible booking statuses")
    public enum BookingStatus {
        @Schema(description = "Booking is pending confirmation")
        PENDING,
        @Schema(description = "Booking has been confirmed")
        CONFIRMED,
        @Schema(description = "Booking has been cancelled")
        CANCELLED,
        @Schema(description = "Booking has been completed")
        COMPLETED
    }
} 