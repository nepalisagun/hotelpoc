package com.galaxyviewtower.hotel.booking.integration;

import com.galaxyviewtower.hotel.booking.dto.CreateBookingDto;
import com.galaxyviewtower.hotel.booking.dto.BookingResponseDto;
import com.galaxyviewtower.hotel.booking.model.BookingStatus;
import com.galaxyviewtower.hotel.booking.model.Hotel;
import com.galaxyviewtower.hotel.booking.repository.BookingRepository;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingServiceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private com.galaxyviewtower.hotel.booking.client.CrudServiceClient crudServiceClient;

    private Hotel testHotel;
    private CreateBookingDto validBookingRequest;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        bookingRepository.deleteAll().block();

        // Setup test hotel
        testHotel = new Hotel();
        testHotel.setId("1");
        testHotel.setName("Test Hotel");
        testHotel.setTotalRooms(10);
        testHotel.setPricePerNight(100.0);

        // Setup valid booking request
        validBookingRequest = new CreateBookingDto();
        validBookingRequest.setUserId("user123");
        validBookingRequest.setHotelId("1");
        validBookingRequest.setCheckIn(LocalDate.now().plusDays(1));
        validBookingRequest.setCheckOut(LocalDate.now().plusDays(3));
        validBookingRequest.setRooms(2);
        validBookingRequest.setSpecialRequests("Test request");

        // Mock CRUD service response
        when(crudServiceClient.getHotelById("1")).thenReturn(Mono.just(testHotel));
    }

    @Test
    void createBooking_Success() {
        webTestClient.post()
                .uri("/api/v1/bookings")
                .bodyValue(validBookingRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingId").exists()
                .jsonPath("$.userId").isEqualTo(validBookingRequest.getUserId())
                .jsonPath("$.hotelId").isEqualTo(validBookingRequest.getHotelId())
                .jsonPath("$.status").isEqualTo(BookingStatus.PENDING.name())
                .jsonPath("$.message").isEqualTo("Booking created successfully");
    }

    @Test
    void createBooking_InvalidHotelId() {
        when(crudServiceClient.getHotelById("invalid-id"))
                .thenReturn(Mono.empty());

        CreateBookingDto request = validBookingRequest;
        request.setHotelId("invalid-id");

        webTestClient.post()
                .uri("/api/v1/bookings")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Hotel not found with id: invalid-id");
    }

    @Test
    void createBooking_InvalidDates() {
        CreateBookingDto request = validBookingRequest;
        request.setCheckIn(LocalDate.now().plusDays(3));
        request.setCheckOut(LocalDate.now().plusDays(1));

        webTestClient.post()
                .uri("/api/v1/bookings")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Check-in date must be before check-out date");
    }

    @Test
    void createBooking_NotEnoughRooms() {
        // Mock existing bookings to simulate room unavailability
        when(crudServiceClient.getHotelById("1")).thenReturn(Mono.just(testHotel));
        
        CreateBookingDto request = validBookingRequest;
        request.setRooms(15); // More rooms than available

        webTestClient.post()
                .uri("/api/v1/bookings")
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Not enough rooms available. Requested: 15, Available: 10");
    }

    @Test
    void getBooking_Success() {
        // First create a booking
        BookingResponseDto createdBooking = webTestClient.post()
                .uri("/api/v1/bookings")
                .bodyValue(validBookingRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookingResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then retrieve it
        webTestClient.get()
                .uri("/api/v1/bookings/" + createdBooking.getBookingId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingId").isEqualTo(createdBooking.getBookingId())
                .jsonPath("$.userId").isEqualTo(validBookingRequest.getUserId())
                .jsonPath("$.hotelId").isEqualTo(validBookingRequest.getHotelId());
    }

    @Test
    void getBooking_NotFound() {
        webTestClient.get()
                .uri("/api/v1/bookings/" + UUID.randomUUID().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void checkAvailability_Success() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/bookings/check-availability")
                        .queryParam("hotelId", "1")
                        .queryParam("checkIn", LocalDate.now().plusDays(1))
                        .queryParam("checkOut", LocalDate.now().plusDays(3))
                        .queryParam("rooms", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }
} 