package com.example.hotel.common.dto;

import java.time.Instant;
import java.time.LocalDate;

public record BookingCreatedEvent(
    String bookingId,
    String userId,
    String hotelId,
    LocalDate checkIn,
    LocalDate checkOut,
    Instant timestamp) {}
