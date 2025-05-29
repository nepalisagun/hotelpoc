package com.galaxyviewtower.hotel.crud.dto;

import com.galaxyviewtower.hotel.crud.validation.HotelValidation;
import com.galaxyviewtower.hotel.crud.validation.ValidBedType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class RoomTypeDTO {
    @NotBlank(message = "Room type ID is required")
    private String id;

    @NotBlank(message = "Hotel ID is required")
    private String hotelId;

    @NotBlank(message = "Room type name is required")
    private String name;

    @NotBlank(message = "Room type description is required")
    private String description;

    @NotNull(message = "Base price per night is required")
    @Positive(message = "Base price must be positive")
    private Double basePricePerNight;

    @NotNull(message = "Total rooms is required")
    @Positive(message = "Total rooms must be positive")
    private Integer totalRooms;

    @NotNull(message = "Maximum occupancy is required")
    @Positive(message = "Maximum occupancy must be positive")
    private Integer maxOccupancy;

    @NotNull(message = "Bed type is required")
    @ValidBedType
    private String bedType;

    @NotNull(message = "Room size is required")
    @Positive(message = "Room size must be positive")
    private Integer roomSize; // in square feet/meters

    @NotNull(message = "Has balcony status is required")
    private Boolean hasBalcony;

    @NotNull(message = "Has ocean view status is required")
    private Boolean hasOceanView;

    @PositiveOrZero(message = "Cancellation fee must be non-negative")
    private Double cancellationFee;

    private String[] amenities;
} 