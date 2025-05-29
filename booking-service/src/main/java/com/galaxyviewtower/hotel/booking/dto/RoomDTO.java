package com.galaxyviewtower.hotel.booking.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private String id;
    private String hotelId;
    private String roomTypeId;
    private String roomNumber;
    private String status;
} 