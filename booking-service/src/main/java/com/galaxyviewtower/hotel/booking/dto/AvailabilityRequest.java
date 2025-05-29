package com.galaxyviewtower.hotel.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityRequest {
    @NotNull(message = "Hotel ID is required")
    private String hotelId;

    @NotNull(message = "Room type ID is required")
    private String roomTypeId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "Number of rooms must be at least 1")
    private Integer numberOfRooms;
} 