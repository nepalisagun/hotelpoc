package com.galaxyviewtower.hotel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Room type request")
public class RoomTypeRequest {
    @NotBlank(message = "Hotel ID is required")
    @Schema(description = "ID of the hotel this room type belongs to")
    private String hotelId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the room type")
    private String name;

    @Schema(description = "Description of the room type")
    private String description;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Schema(description = "Maximum number of guests allowed")
    private Integer capacity;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", message = "Base price must be greater than or equal to 0")
    @Schema(description = "Base price per night")
    private BigDecimal basePricePerNight;

    @DecimalMin(value = "0.0", message = "Size must be greater than 0")
    @Schema(description = "Room size in square meters")
    private BigDecimal sizeSqm;

    @Schema(description = "Type of bed(s) in the room")
    private String bedType;

    @Schema(description = "Type of view from the room")
    private String viewType;
} 