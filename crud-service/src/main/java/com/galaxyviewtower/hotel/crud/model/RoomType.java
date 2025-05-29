package com.galaxyviewtower.hotel.crud.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("room_types")
public class RoomType {
    @Id
    private String id;
    private String hotelId;
    private String name;
    private String description;
    private Integer capacity;
    private BigDecimal basePricePerNight;
    private BigDecimal sizeSqm;
    private String bedType;
    private String viewType;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 