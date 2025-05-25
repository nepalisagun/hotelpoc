package com.example.hotel.common.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder // Using builder for easier DTO creation
public record HotelDto(String id, String name, String city, BigDecimal pricePerNight) {}
