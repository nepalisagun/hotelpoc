package com.galaxyviewtower.hotel.booking.dto;

import com.galaxyviewtower.hotel.booking.model.Booking;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private String id;
    
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Hotel ID is required")
    private String hotelId;
    
    @NotNull(message = "Room type ID is required")
    private String roomTypeId;
    
    private String roomId;
    
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer numberOfGuests;
    
    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;
    
    private Booking.BookingStatus status;
    private LocalDateTime bookedAt;
    private LocalDateTime updatedAt;
} 