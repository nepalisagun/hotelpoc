package com.galaxyviewtower.hotel.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AvailabilityResponse {
    private boolean available;
    private String hotelId;
    private String roomTypeId;
    private String roomTypeName;
    private Integer totalRooms;
    private Integer availableRooms;
    private BigDecimal pricePerNight;
    private List<String> availableRoomNumbers;
    private String message;
} 