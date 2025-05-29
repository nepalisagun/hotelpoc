package com.galaxyviewtower.hotel.crud.model;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("hotels") // Maps to the H2 table (plural)
public class Hotel {
  @Id private String id;

  @NotBlank(message = "Hotel name is required")
  @Size(min = 2, max = 100, message = "Hotel name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Address is required")
  @Size(max = 255, message = "Address must not exceed 255 characters")
  private String address; // was city

  @NotBlank(message = "City is required")
  @Size(max = 100, message = "City must not exceed 100 characters")
  private String city;

  @NotBlank(message = "Country is required")
  @Size(max = 100, message = "Country must not exceed 100 characters")
  private String country;

  @NotNull(message = "Rating is required")
  @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
  @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
  private BigDecimal rating; // was pricePerNight

  @NotNull(message = "Total rooms is required")
  @Min(value = 1, message = "Hotel must have at least 1 room")
  @Max(value = 10000, message = "Hotel cannot have more than 10000 rooms")
  private int totalRooms;

  @NotNull(message = "Price per night is required")
  @DecimalMin(value = "0.0", message = "Price per night must be at least 0.0")
  @DecimalMax(value = "100000.0", message = "Price per night must not exceed 100000.0")
  private BigDecimal pricePerNight;

  // Additional fields for a production hotel system
  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  private String phoneNumber;

  @Email(message = "Invalid email format")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String description;

  @Size(max = 1000, message = "Amenities must not exceed 1000 characters")
  private String amenities;

  @NotNull(message = "Check-in time is required")
  private String checkInTime;

  @NotNull(message = "Check-out time is required")
  private String checkOutTime;

  private boolean isActive = true;
}
