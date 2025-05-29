package com.galaxyviewtower.hotel.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRoomAssignmentDTO {
    private String id;
    
    @NotNull(message = "Booking ID is required")
    private String bookingId;
    
    @NotNull(message = "Room ID is required")
    private String roomId;
    
    private LocalDateTime assignedDate;
} 