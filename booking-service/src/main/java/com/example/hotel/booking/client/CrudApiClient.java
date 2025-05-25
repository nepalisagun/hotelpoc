package com.example.hotel.booking.client;

import com.example.hotel.common.dto.HotelDto; // <-- Using our common DTO
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrudApiClient {
  private final WebClient crudWebClient;

  public Mono<HotelDto> getHotelById(String hotelId) {
    log.info("BOOKING: Calling CRUD service for hotel ID: {}", hotelId);
    return crudWebClient
        .get()
        .uri("/hotels/{hotelId}", hotelId)
        .retrieve()
        .bodyToMono(HotelDto.class) // Assumes CRUD service can return our common DTO
        .doOnError(e -> log.error("BOOKING: Error calling CRUD service: {}", e.getMessage()));
  }
}
