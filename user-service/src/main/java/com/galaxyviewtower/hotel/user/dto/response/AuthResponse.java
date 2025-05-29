package com.galaxyviewtower.hotel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing authentication tokens and user information")
public class AuthResponse {

    @Schema(description = "JWT access token for API authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    private String token;

    @Schema(description = "JWT refresh token for obtaining new access tokens", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    private String refreshToken;

    @Schema(description = "User information", required = true)
    private UserResponse user;

    @Schema(description = "Token expiration time in milliseconds", 
            example = "3600000",
            required = true)
    private long expiresIn;
} 