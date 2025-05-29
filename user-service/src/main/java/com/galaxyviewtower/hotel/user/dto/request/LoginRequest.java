package com.galaxyviewtower.hotel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for user login")
public class LoginRequest {

    @Schema(description = "Username or email address for login", 
            example = "john.doe@example.com", 
            required = true)
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @Schema(description = "Password for authentication", 
            example = "SecurePass123!", 
            required = true,
            format = "password")
    @NotBlank(message = "Password is required")
    private String password;
} 