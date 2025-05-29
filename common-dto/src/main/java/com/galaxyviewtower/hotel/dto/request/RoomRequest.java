package com.galaxyviewtower.hotel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Room request")
public class RoomRequest {
    @NotBlank(message = "Hotel ID is required")
    @Schema(description = "ID of the hotel this room belongs to")
    private String hotelId;

    @NotBlank(message = "Room type ID is required")
    @Schema(description = "ID of the room type")
    private String roomTypeId;

    @NotBlank(message = "Room number is required")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Room number can only contain letters, numbers, and hyphens")
    @Schema(description = "Room number")
    private String roomNumber;

    @NotNull(message = "Floor number is required")
    @Min(value = 0, message = "Floor number must be greater than or equal to 0")
    @Schema(description = "Floor number")
    private Integer floorNumber;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(AVAILABLE|OCCUPIED|MAINTENANCE|CLEANING)$", 
            message = "Status must be one of: AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING")
    @Schema(description = "Current status of the room")
    private String status;
} 