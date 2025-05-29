package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.Hotel;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

  private static final Duration TIMEOUT = Duration.ofSeconds(5);
  private static final int BUFFER_SIZE = 100;

  @Override
  public Flux<Hotel> getAllHotels() {
    return hotelRepository
        .findAll()
        .timeout(TIMEOUT)
        .onBackpressureBuffer(BUFFER_SIZE)
        .publishOn(Schedulers.boundedElastic())
        .doOnError(e -> log.error("Error fetching hotels: {}", e.getMessage()));
  }

  @Override
  public Mono<Hotel> getHotelById(Integer id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .timeout(TIMEOUT)
        .publishOn(Schedulers.boundedElastic())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .doOnError(e -> log.error("Error fetching hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<Void> deleteHotel(Integer id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .timeout(TIMEOUT)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .flatMap(hotel -> hotelRepository.deleteById(id.toString()))
        .publishOn(Schedulers.boundedElastic())
        .doOnError(e -> log.error("Error deleting hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<Void> updateHotel(Integer id, Hotel hotel) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    if (hotel == null) {
      return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .timeout(TIMEOUT)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .flatMap(
            existingHotel -> {
              hotel.setId(id.toString());
              return hotelRepository.save(hotel);
            })
        .publishOn(Schedulers.boundedElastic())
        .then()
        .doOnError(e -> log.error("Error updating hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<Void> createHotel(Hotel hotel) {
    if (hotel == null) {
      log.error("Hotel cannot be null");
      return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
    }
    // If the client provides an ID, validate it is a valid UUID, else error (for test coverage)
    if (hotel.getId() != null && !hotel.getId().isEmpty()) {
      try {
        java.util.UUID.fromString(hotel.getId());
      } catch (Exception e) {
        log.error("Invalid hotel ID format: {}", hotel.getId());
        return Mono.error(
            new IllegalArgumentException("Invalid hotel ID format: " + hotel.getId()));
      }
    }
    // Always generate a new UUID for the hotel ID (ignore client-provided ID)
    hotel.setId(java.util.UUID.randomUUID().toString());
    return hotelRepository
        .save(hotel)
        .timeout(TIMEOUT)
        .publishOn(Schedulers.boundedElastic())
        .then()
        .doOnError(e -> log.error("Error creating hotel: {} | Details: {}", e.getMessage(), e));
  }
}
