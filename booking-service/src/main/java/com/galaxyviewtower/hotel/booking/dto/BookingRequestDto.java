package com.galaxyviewtower.hotel.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request object for creating a new booking")
public class BookingRequestDto {
    @Schema(description = "ID of the hotel to book", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @NotBlank(message = "Hotel ID is required")
    private String hotelId;

    @Schema(description = "ID of the user making the booking", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Check-in date (yyyy-MM-dd)", example = "2024-06-01", required = true)
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @Schema(description = "Check-out date (yyyy-MM-dd)", example = "2024-06-05", required = true)
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Schema(description = "Number of rooms to book", example = "2", minimum = "1", required = true)
    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "At least one room must be booked")
    private Integer rooms;

    @Schema(description = "Special requests or notes for the booking", example = "Please provide a room with ocean view")
    private String specialRequests;
} 