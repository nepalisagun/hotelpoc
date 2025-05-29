package com.galaxyviewtower.hotel.booking.validation;

import com.galaxyviewtower.hotel.booking.dto.request.BookingRequestDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidationTest {

    private Validator validator;
    private BookingRequestDto bookingRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        bookingRequest = new BookingRequestDto();
        bookingRequest.setHotelId("test-hotel-id");
        bookingRequest.setRoomTypeId("test-room-type-id");
        bookingRequest.setUserId("test-user-id");
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(2));
        bookingRequest.setNumberOfGuests(2);
    }

    @Test
    void testValidBookingRequest() {
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertTrue(violations.isEmpty(), "Valid booking request should have no violations");
    }

    @Test
    void testInvalidHotelId() {
        bookingRequest.setHotelId("");
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertFalse(violations.isEmpty(), "Empty hotel ID should be invalid");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hotelId")));
    }

    @Test
    void testInvalidRoomTypeId() {
        bookingRequest.setRoomTypeId(null);
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertFalse(violations.isEmpty(), "Null room type ID should be invalid");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("roomTypeId")));
    }

    @Test
    void testInvalidCheckInDate() {
        bookingRequest.setCheckInDate(LocalDate.now().minusDays(1));
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertFalse(violations.isEmpty(), "Past check-in date should be invalid");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("checkInDate")));
    }

    @Test
    void testInvalidCheckOutDate() {
        bookingRequest.setCheckOutDate(bookingRequest.getCheckInDate().minusDays(1));
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertFalse(violations.isEmpty(), "Check-out date before check-in date should be invalid");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("checkOutDate")));
    }

    @Test
    void testInvalidNumberOfGuests() {
        bookingRequest.setNumberOfGuests(0);
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertFalse(violations.isEmpty(), "Zero guests should be invalid");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberOfGuests")));
    }

    @Test
    void testInvalidContactInfo() {
        bookingRequest.getContactInfo().setEmail("invalid-email");
        Set<jakarta.validation.ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingRequest);
        assertFalse(violations.isEmpty(), "Invalid email should be rejected");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactInfo.email")));
    }
} 