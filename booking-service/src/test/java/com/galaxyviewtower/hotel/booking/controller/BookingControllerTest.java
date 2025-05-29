package com.galaxyviewtower.hotel.booking.controller;

import com.galaxyviewtower.hotel.booking.dto.request.BookingRequestDto;
import com.galaxyviewtower.hotel.booking.dto.response.BookingResponseDto;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDto testBookingRequest;
    private BookingResponseDto testBookingResponse;

    @BeforeEach
    void setUp() {
        testBookingRequest = new BookingRequestDto();
        testBookingRequest.setHotelId("test-hotel-id");
        testBookingRequest.setRoomTypeId("test-room-type-id");
        testBookingRequest.setUserId("test-user-id");
        testBookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        testBookingRequest.setCheckOutDate(LocalDate.now().plusDays(2));
        testBookingRequest.setNumberOfGuests(2);

        testBookingResponse = new BookingResponseDto();
        testBookingResponse.setId("test-booking-id");
        testBookingResponse.setHotelId(testBookingRequest.getHotelId());
        testBookingResponse.setRoomTypeId(testBookingRequest.getRoomTypeId());
        testBookingResponse.setUserId(testBookingRequest.getUserId());
        testBookingResponse.setCheckInDate(testBookingRequest.getCheckInDate());
        testBookingResponse.setCheckOutDate(testBookingRequest.getCheckOutDate());
        testBookingResponse.setNumberOfGuests(testBookingRequest.getNumberOfGuests());
        testBookingResponse.setTotalPrice(new BigDecimal("299.99"));
        testBookingResponse.setStatus(BookingResponseDto.BookingStatus.PENDING);
        testBookingResponse.setBookedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBooking() {
        when(bookingService.createBooking(any(BookingRequestDto.class)))
                .thenReturn(Mono.just(testBookingResponse));

        webTestClient.post()
                .uri("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testBookingRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testBookingResponse.getId())
                .jsonPath("$.hotelId").isEqualTo(testBookingResponse.getHotelId())
                .jsonPath("$.status").isEqualTo(testBookingResponse.getStatus().toString());
    }

    @Test
    void testGetBooking() {
        when(bookingService.getBooking("test-booking-id"))
                .thenReturn(Mono.just(testBookingResponse));

        webTestClient.get()
                .uri("/api/bookings/test-booking-id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testBookingResponse.getId())
                .jsonPath("$.hotelId").isEqualTo(testBookingResponse.getHotelId());
    }

    @Test
    void testCancelBooking() {
        when(bookingService.cancelBooking("test-booking-id"))
                .thenReturn(Mono.just(testBookingResponse));

        webTestClient.post()
                .uri("/api/bookings/test-booking-id/cancel")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testBookingResponse.getId())
                .jsonPath("$.status").isEqualTo(testBookingResponse.getStatus().toString());
    }

    @Test
    void testGetUserBookings() {
        when(bookingService.getUserBookings("test-user-id"))
                .thenReturn(Mono.just(testBookingResponse));

        webTestClient.get()
                .uri("/api/bookings/user/test-user-id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testBookingResponse.getId())
                .jsonPath("$.userId").isEqualTo(testBookingResponse.getUserId());
    }

    @Test
    void testCheckRoomAvailability() {
        when(bookingService.checkRoomAvailability(
                any(String.class),
                any(String.class),
                any(LocalDate.class),
                any(LocalDate.class),
                any(Integer.class)
        )).thenReturn(Mono.just(true));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/bookings/availability")
                        .queryParam("hotelId", "test-hotel-id")
                        .queryParam("roomTypeId", "test-room-type-id")
                        .queryParam("checkInDate", LocalDate.now().plusDays(1))
                        .queryParam("checkOutDate", LocalDate.now().plusDays(2))
                        .queryParam("numberOfRooms", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.available").isEqualTo(true);
    }
} 