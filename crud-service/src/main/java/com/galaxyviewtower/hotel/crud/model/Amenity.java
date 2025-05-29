package com.galaxyviewtower.hotel.crud.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Table("amenities")
public class Amenity {
    @Id
    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 