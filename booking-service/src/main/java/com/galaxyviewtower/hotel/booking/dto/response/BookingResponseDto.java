package com.galaxyviewtower.hotel.booking.dto.response;

import com.galaxyviewtower.hotel.booking.model.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private String id;
    private String hotelId;
    private String hotelName;
    private String roomTypeId;
    private String roomTypeName;
    private String userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private String specialRequests;
    private BigDecimal totalPrice;
    private Booking.BookingStatus status;
    private LocalDateTime bookedAt;
    private LocalDateTime updatedAt;
    private ContactInfoDto contactInfo;
    private PaymentInfoDto paymentInfo;

    @Data
    public static class ContactInfoDto {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String address;
    }

    @Data
    public static class PaymentInfoDto {
        private String paymentId;
        private String paymentStatus;
        private LocalDateTime paymentDate;
        private BigDecimal amount;
        private String currency;
    }
} 