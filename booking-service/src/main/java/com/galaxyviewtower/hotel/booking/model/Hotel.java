package com.galaxyviewtower.hotel.booking.model;

import lombok.Data;

@Data
public class Hotel {
    private String id;
    private String name;
    private String address;
    private String city;
    private String country;
    private double rating;
    private int totalRooms;
    private double pricePerNight;
}