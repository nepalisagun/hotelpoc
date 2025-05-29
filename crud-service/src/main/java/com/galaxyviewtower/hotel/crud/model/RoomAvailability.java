package com.galaxyviewtower.hotel.crud.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table("room_availability")
public class RoomAvailability {
    @Id
    private String id;
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String bookingId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 