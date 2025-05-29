package com.galaxyviewtower.hotel.booking.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("booking_room_assignments")
public class BookingRoomAssignment {
    @Id
    private String id;
    private String bookingId;
    private String roomId;
    private LocalDateTime assignedDate;
} 