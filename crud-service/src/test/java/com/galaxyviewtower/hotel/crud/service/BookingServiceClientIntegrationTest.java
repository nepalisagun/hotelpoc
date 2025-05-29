package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.WebClientConfig;
import com.galaxyviewtower.hotel.crud.dto.BookingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceClientIntegrationTest {

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @MockBean
    private WebClient webClient;

    @MockBean
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @MockBean
    private WebClient.RequestBodySpec requestBodySpec;

    @MockBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestBodyUriSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(webClient.delete()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        // Arrange
        String bookingId = UUID.randomUUID().toString();
        BookingDTO expectedBooking = createSampleBooking(bookingId);
        when(responseSpec.bodyToMono(BookingDTO.class)).thenReturn(Mono.just(expectedBooking));

        // Act & Assert
        StepVerifier.create(bookingServiceClient.getBookingById(bookingId))
            .expectNext(expectedBooking)
            .verifyComplete();
    }

    @Test
    void createBooking_ShouldCreateNewBooking() {
        // Arrange
        BookingDTO bookingDTO = createSampleBooking(null);
        when(responseSpec.bodyToMono(BookingDTO.class)).thenReturn(Mono.just(bookingDTO));

        // Act & Assert
        StepVerifier.create(bookingServiceClient.createBooking(bookingDTO))
            .expectNext(bookingDTO)
            .verifyComplete();
    }

    @Test
    void updateBooking_ShouldUpdateExistingBooking() {
        // Arrange
        String bookingId = UUID.randomUUID().toString();
        BookingDTO bookingDTO = createSampleBooking(bookingId);
        when(responseSpec.bodyToMono(BookingDTO.class)).thenReturn(Mono.just(bookingDTO));

        // Act & Assert
        StepVerifier.create(bookingServiceClient.updateBooking(bookingId, bookingDTO))
            .expectNext(bookingDTO)
            .verifyComplete();
    }

    @Test
    void cancelBooking_ShouldCancelBooking() {
        // Arrange
        String bookingId = UUID.randomUUID().toString();
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(bookingServiceClient.cancelBooking(bookingId))
            .verifyComplete();
    }

    @Test
    void getBookingById_ShouldUseFallbackWhenServiceUnavailable() {
        // Arrange
        String bookingId = UUID.randomUUID().toString();
        when(responseSpec.bodyToMono(BookingDTO.class))
            .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        // Act & Assert
        StepVerifier.create(bookingServiceClient.getBookingById(bookingId))
            .expectNextMatches(booking -> 
                booking.getId().equals(bookingId) &&
                booking.getStatus().equals("FALLBACK"))
            .verifyComplete();
    }

    private BookingDTO createSampleBooking(String id) {
        return BookingDTO.builder()
            .id(id != null ? id : UUID.randomUUID().toString())
            .hotelId(UUID.randomUUID().toString())
            .userId(UUID.randomUUID().toString())
            .checkIn(LocalDateTime.now().plusDays(1))
            .checkOut(LocalDateTime.now().plusDays(3))
            .status("CONFIRMED")
            .createdAt(LocalDateTime.now())
            .build();
    }
} 