package com.galaxyviewtower.hotel.crud.client;

import com.galaxyviewtower.hotel.crud.dto.BookingDTO;
import com.galaxyviewtower.hotel.crud.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BookingServiceClient {

    private final WebClient bookingServiceClient;

    @Autowired
    public BookingServiceClient(WebClient bookingServiceClient) {
        this.bookingServiceClient = bookingServiceClient;
    }

    @CircuitBreaker(name = "bookingService", fallbackMethod = "getBookingFallback")
    @Retry(name = "bookingService", fallbackMethod = "getBookingFallback")
    public Mono<BookingDTO> getBookingById(String bookingId) {
        return bookingServiceClient
                .get()
                .uri("/api/v1/bookings/{id}", bookingId)
                .retrieve()
                .bodyToMono(BookingDTO.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                });
    }

    @CircuitBreaker(name = "bookingService", fallbackMethod = "validateBookingFallback")
    @Retry(name = "bookingService", fallbackMethod = "validateBookingFallback")
    public Mono<Boolean> validateBooking(String bookingId) {
        return bookingServiceClient
                .get()
                .uri("/api/v1/bookings/{id}/validate", bookingId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.NOT_FOUND) {
                        return Mono.just(false);
                    }
                    return Mono.error(e);
                });
    }

    @CircuitBreaker(name = "bookingService", fallbackMethod = "checkAvailabilityFallback")
    @Retry(name = "bookingService", fallbackMethod = "checkAvailabilityFallback")
    public Mono<Boolean> checkRoomAvailability(String roomId, String checkIn, String checkOut) {
        return bookingServiceClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/bookings/availability")
                        .queryParam("roomId", roomId)
                        .queryParam("checkIn", checkIn)
                        .queryParam("checkOut", checkOut)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.BAD_REQUEST) {
                        return Mono.just(false);
                    }
                    return Mono.error(e);
                });
    }

    // Fallback methods
    private Mono<BookingDTO> getBookingFallback(String bookingId, Exception e) {
        return Mono.error(new ServiceException(
                "Failed to get booking: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e));
    }

    private Mono<Boolean> validateBookingFallback(String bookingId, Exception e) {
        return Mono.just(false);
    }

    private Mono<Boolean> checkAvailabilityFallback(String roomId, String checkIn, String checkOut, Exception e) {
        return Mono.just(false);
    }
} 