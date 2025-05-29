package com.galaxyviewtower.hotel.booking.client;

import com.galaxyviewtower.hotel.booking.dto.HotelDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomTypeDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrudServiceClientImpl implements CrudServiceClient {

    private final WebClient webClient;

    @Value("${services.crud-service.url}")
    private String crudServiceUrl;

    @Override
    @CircuitBreaker(name = "crudService", fallbackMethod = "getHotelFallback")
    @Retry(name = "crudService")
    public Mono<HotelDTO> getHotel(String hotelId) {
        return webClient.get()
                .uri(crudServiceUrl + "/api/v1/hotels/{hotelId}", hotelId)
                .retrieve()
                .bodyToMono(HotelDTO.class)
                .doOnError(error -> log.error("Error fetching hotel {}: {}", hotelId, error.getMessage()));
    }

    @Override
    @CircuitBreaker(name = "crudService", fallbackMethod = "getRoomTypeFallback")
    @Retry(name = "crudService")
    public Mono<RoomTypeDTO> getRoomType(String roomTypeId) {
        return webClient.get()
                .uri(crudServiceUrl + "/api/v1/room-types/{roomTypeId}", roomTypeId)
                .retrieve()
                .bodyToMono(RoomTypeDTO.class)
                .doOnError(error -> log.error("Error fetching room type {}: {}", roomTypeId, error.getMessage()));
    }

    @Override
    @CircuitBreaker(name = "crudService", fallbackMethod = "checkRoomAvailabilityFallback")
    @Retry(name = "crudService")
    public Mono<Boolean> checkRoomAvailability(String hotelId, String roomTypeId, String checkInDate, String checkOutDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(crudServiceUrl + "/api/v1/room-types/{roomTypeId}/availability")
                        .queryParam("hotelId", hotelId)
                        .queryParam("checkInDate", checkInDate)
                        .queryParam("checkOutDate", checkOutDate)
                        .build(roomTypeId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(error -> log.error("Error checking room availability: {}", error.getMessage()));
    }

    // Fallback methods
    private Mono<HotelDTO> getHotelFallback(String hotelId, Exception e) {
        log.error("Fallback: Error fetching hotel {}: {}", hotelId, e.getMessage());
        return Mono.error(new RuntimeException("Hotel service is currently unavailable"));
    }

    private Mono<RoomTypeDTO> getRoomTypeFallback(String roomTypeId, Exception e) {
        log.error("Fallback: Error fetching room type {}: {}", roomTypeId, e.getMessage());
        return Mono.error(new RuntimeException("Room type service is currently unavailable"));
    }

    private Mono<Boolean> checkRoomAvailabilityFallback(String hotelId, String roomTypeId, String checkInDate, String checkOutDate, Exception e) {
        log.error("Fallback: Error checking room availability: {}", e.getMessage());
        return Mono.error(new RuntimeException("Room availability service is currently unavailable"));
    }
} 