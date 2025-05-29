package com.galaxyviewtower.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private String userId;
    private String hotelId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;
    
    private int rooms;
    private String specialRequests;
} 