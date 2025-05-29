package com.galaxyviewtower.hotel.crud.dto;

import com.galaxyviewtower.hotel.crud.validation.ValidRoomNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoomDTO {
    @NotBlank(message = "Room ID is required")
    private String id;

    @NotBlank(message = "Hotel ID is required")
    private String hotelId;

    @NotBlank(message = "Room type ID is required")
    private String roomTypeId;

    @NotBlank(message = "Room number is required")
    @ValidRoomNumber
    private String roomNumber;

    @NotNull(message = "Floor number is required")
    @Positive(message = "Floor number must be positive")
    private Integer floor;

    @NotNull(message = "Room status is required")
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE

    private String notes;
} 