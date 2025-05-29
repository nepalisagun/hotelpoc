package com.galaxyviewtower.hotel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for user registration")
public class UserRegistrationRequest {

    @Schema(description = "Username for the user account", 
            example = "john.doe", 
            required = true,
            minLength = 3,
            maxLength = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;

    @Schema(description = "Email address of the user", 
            example = "john.doe@example.com", 
            required = true,
            format = "email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password for the user account", 
            example = "SecurePass123!", 
            required = true,
            minLength = 8,
            maxLength = 100,
            format = "password")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", 
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;

    @Schema(description = "First name of the user", 
            example = "John", 
            required = true,
            minLength = 2,
            maxLength = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Last name of the user", 
            example = "Doe", 
            required = true,
            minLength = 2,
            maxLength = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
} 