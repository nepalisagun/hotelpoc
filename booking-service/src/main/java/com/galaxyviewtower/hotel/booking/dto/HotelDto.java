package com.galaxyviewtower.hotel.booking.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotelDTO {
    private String id;
    private String name;
    private String address;
    private String city;
    private String country;
    private BigDecimal rating;
    private Integer totalRooms;
    private BigDecimal pricePerNight;
    private String phoneNumber;
    private String email;
    private String description;
    private String amenities;
    private String checkInTime;
    private String checkOutTime;
    private Boolean isActive;
} 