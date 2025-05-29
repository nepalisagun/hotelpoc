package com.galaxyviewtower.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private String bookingId;
    private String userId;
    private String hotelId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;
    
    private int rooms;
    private double totalPrice;
    private BookingStatus status;
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
} 