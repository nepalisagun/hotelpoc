package com.galaxyviewtower.hotel.user.dto.response;

import com.galaxyviewtower.hotel.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Response object containing user information")
public class UserResponse {

    @Schema(description = "Unique identifier of the user", 
            example = "123e4567-e89b-12d3-a456-426614174000",
            required = true)
    private String id;

    @Schema(description = "Username of the user", 
            example = "john.doe",
            required = true)
    private String username;

    @Schema(description = "Email address of the user", 
            example = "john.doe@example.com",
            required = true)
    private String email;

    @Schema(description = "First name of the user", 
            example = "John",
            required = true)
    private String firstName;

    @Schema(description = "Last name of the user", 
            example = "Doe",
            required = true)
    private String lastName;

    @Schema(description = "Role of the user in the system", 
            example = "CUSTOMER",
            required = true,
            allowableValues = {"ADMIN", "STAFF", "CUSTOMER"})
    private User.UserRole role;

    @Schema(description = "Whether the user account is active", 
            example = "true",
            required = true)
    private boolean active;

    @Schema(description = "Timestamp of the user's last login", 
            example = "2024-03-15T10:30:00",
            required = true)
    private LocalDateTime lastLogin;

    @Schema(description = "Timestamp when the user account was created", 
            example = "2024-03-15T10:30:00",
            required = true)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the user account was last updated", 
            example = "2024-03-15T10:30:00",
            required = true)
    private LocalDateTime updatedAt;

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
} 