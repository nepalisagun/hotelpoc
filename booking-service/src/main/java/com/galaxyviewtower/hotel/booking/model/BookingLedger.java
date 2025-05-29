package com.galaxyviewtower.hotel.booking.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table("booking_ledger")
public class BookingLedger {
    @Id
    private String id;
    private String hotelId;
    private String roomTypeId;
    private String roomId;
    private String bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status; // CONFIRMED, CANCELLED, COMPLETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 