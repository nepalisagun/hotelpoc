package com.galaxyviewtower.hotel.crud.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Table("rooms")
public class Room {
    @Id
    private String id;
    private String hotelId;
    private String roomTypeId;
    private String roomNumber;
    private RoomStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum RoomStatus {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE,
        CLEANING
    }
} 