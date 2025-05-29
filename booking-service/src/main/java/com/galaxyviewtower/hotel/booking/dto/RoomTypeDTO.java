package com.galaxyviewtower.hotel.booking.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomTypeDTO {
    private String id;
    private String hotelId;
    private String name;
    private String description;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private Integer totalRooms;
    private String amenities;
    private Boolean isActive;
} 