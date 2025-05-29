package com.galaxyviewtower.hotel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Amenity request")
public class AmenityRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the amenity")
    private String name;

    @Schema(description = "Description of the amenity")
    private String description;

    @Schema(description = "Icon identifier for the amenity")
    private String icon;
} 