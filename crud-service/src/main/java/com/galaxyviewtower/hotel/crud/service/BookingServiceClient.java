package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.WebClientConfig;
import com.galaxyviewtower.hotel.crud.dto.BookingDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class BookingServiceClient {

    private final WebClient webClient;
    private final WebClientConfig webClientConfig;

    public BookingServiceClient(WebClient bookingServiceWebClient, WebClientConfig webClientConfig) {
        this.webClient = bookingServiceWebClient;
        this.webClientConfig = webClientConfig;
    }

    public Mono<BookingDTO> getBookingById(String bookingId) {
        return webClient.get()
            .uri("/api/bookings/{id}", bookingId)
            .retrieve()
            .bodyToMono(BookingDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("bookingService"))
            .onErrorResume(CallNotPermittedException.class, e -> getFallbackBooking(bookingId));
    }

    public Mono<BookingDTO> createBooking(BookingDTO bookingDTO) {
        return webClient.post()
            .uri("/api/bookings")
            .bodyValue(bookingDTO)
            .retrieve()
            .bodyToMono(BookingDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("bookingService"))
            .onErrorResume(CallNotPermittedException.class, e -> createFallbackBooking(bookingDTO));
    }

    public Mono<BookingDTO> updateBooking(String bookingId, BookingDTO bookingDTO) {
        return webClient.put()
            .uri("/api/bookings/{id}", bookingId)
            .bodyValue(bookingDTO)
            .retrieve()
            .bodyToMono(BookingDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("bookingService"))
            .onErrorResume(CallNotPermittedException.class, e -> updateFallbackBooking(bookingId, bookingDTO));
    }

    public Mono<Void> cancelBooking(String bookingId) {
        return webClient.delete()
            .uri("/api/bookings/{id}", bookingId)
            .retrieve()
            .bodyToMono(Void.class)
            .transform(webClientConfig.applyResiliencePatterns("bookingService"))
            .onErrorResume(CallNotPermittedException.class, e -> cancelFallbackBooking(bookingId));
    }

    @Cacheable(value = "bookingFallback", key = "#bookingId", unless = "#result == null")
    private Mono<BookingDTO> getFallbackBooking(String bookingId) {
        return Mono.just(BookingDTO.builder()
            .id(bookingId)
            .status("FALLBACK")
            .message("Service temporarily unavailable. Using cached data.")
            .build());
    }

    @Cacheable(value = "bookingFallback", key = "#bookingDTO.id", unless = "#result == null")
    private Mono<BookingDTO> createFallbackBooking(BookingDTO bookingDTO) {
        return Mono.just(BookingDTO.builder()
            .id("FALLBACK-" + System.currentTimeMillis())
            .hotelId(bookingDTO.getHotelId())
            .userId(bookingDTO.getUserId())
            .checkIn(bookingDTO.getCheckIn())
            .checkOut(bookingDTO.getCheckOut())
            .status("PENDING")
            .message("Service temporarily unavailable. Booking will be processed when service is restored.")
            .createdAt(LocalDateTime.now())
            .build());
    }

    @Cacheable(value = "bookingFallback", key = "#bookingId", unless = "#result == null")
    private Mono<BookingDTO> updateFallbackBooking(String bookingId, BookingDTO bookingDTO) {
        return Mono.just(BookingDTO.builder()
            .id(bookingId)
            .hotelId(bookingDTO.getHotelId())
            .userId(bookingDTO.getUserId())
            .checkIn(bookingDTO.getCheckIn())
            .checkOut(bookingDTO.getCheckOut())
            .status("PENDING_UPDATE")
            .message("Service temporarily unavailable. Update will be processed when service is restored.")
            .updatedAt(LocalDateTime.now())
            .build());
    }

    private Mono<Void> cancelFallbackBooking(String bookingId) {
        return Mono.empty();
    }
} 