package com.galaxyviewtower.hotel.crud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hotel data transfer object")
public class HotelDTO {

    @Schema(description = "Unique identifier of the hotel", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @NotBlank(message = "Hotel name is required")
    @Size(min = 2, max = 100, message = "Hotel name must be between 2 and 100 characters")
    @Schema(description = "Name of the hotel", example = "Grand Hotel", required = true)
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Schema(description = "Physical address of the hotel", example = "123 Main Street", required = true)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    @Schema(description = "City where the hotel is located", example = "New York", required = true)
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country name must not exceed 100 characters")
    @Schema(description = "Country where the hotel is located", example = "USA", required = true)
    private String country;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    @Schema(description = "Hotel rating (0.0 to 5.0)", example = "4.5", required = true)
    private BigDecimal rating;

    @NotNull(message = "Total rooms is required")
    @Min(value = 1, message = "Total rooms must be at least 1")
    @Max(value = 10000, message = "Total rooms must not exceed 10000")
    @Schema(description = "Total number of rooms in the hotel", example = "200", required = true)
    private Integer totalRooms;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.0", message = "Price must be at least 0.0")
    @DecimalMax(value = "100000.0", message = "Price must not exceed 100000.0")
    @Schema(description = "Price per night in the hotel's currency", example = "299.99", required = true)
    private BigDecimal pricePerNight;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Schema(description = "Contact phone number", example = "+1-555-0123")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Schema(description = "Contact email address", example = "info@grandhotel.com")
    private String email;

    @Schema(description = "Detailed description of the hotel", example = "Luxury hotel in the heart of Manhattan")
    private String description;

    @Schema(description = "List of amenities offered by the hotel", example = "[\"Pool\", \"Spa\", \"Restaurant\", \"Gym\"]")
    private List<String> amenities;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid check-in time format (HH:mm)")
    @Schema(description = "Check-in time in 24-hour format", example = "14:00", required = true)
    private String checkInTime;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid check-out time format (HH:mm)")
    @Schema(description = "Check-out time in 24-hour format", example = "12:00", required = true)
    private String checkOutTime;

    @Schema(description = "Whether the hotel is currently active", example = "true")
    private Boolean isActive;
} 