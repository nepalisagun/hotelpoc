package com.galaxyviewtower.hotel.booking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {
    @NotBlank(message = "Hotel ID is required")
    private String hotelId;

    @NotBlank(message = "Room type ID is required")
    private String roomTypeId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer numberOfGuests;

    private String specialRequests;

    @NotNull(message = "Contact information is required")
    private ContactInfoDto contactInfo;

    @Data
    public static class ContactInfoDto {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        private String address;
    }
} 