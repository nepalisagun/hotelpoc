package com.galaxyviewtower.hotel.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("email_templates")
public class EmailTemplate {
    @Id
    private String id;
    private String name;
    private String subject;
    private String body;
    private String type;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 