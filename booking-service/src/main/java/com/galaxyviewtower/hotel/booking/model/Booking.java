package com.galaxyviewtower.hotel.booking.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table("bookings")
public class Booking {
    @Id
    private String id;
    private String userId;
    private String hotelId;
    private String roomTypeId;
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private LocalDateTime bookedAt;
    private LocalDateTime updatedAt;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }
} 