package com.galaxyviewtower.hotel.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("users")
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum UserRole {
        ADMIN,
        STAFF,
        CUSTOMER
    }
} 