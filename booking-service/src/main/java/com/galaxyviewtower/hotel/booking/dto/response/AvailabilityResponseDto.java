package com.galaxyviewtower.hotel.booking.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AvailabilityResponseDto {
    private boolean available;
    private String hotelId;
    private String hotelName;
    private String roomTypeId;
    private String roomTypeName;
    private Integer availableRooms;
    private BigDecimal pricePerNight;
    private List<RoomAvailabilityDto> roomAvailability;

    @Data
    public static class RoomAvailabilityDto {
        private String roomId;
        private String roomNumber;
        private boolean available;
        private String unavailableReason;
    }
} 