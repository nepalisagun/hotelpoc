package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.Hotel;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

  @Override
  public Flux<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  @Override
  public Mono<Hotel> getHotelById(Integer id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)));
  }

  @Override
  public Mono<Void> deleteHotel(Integer id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .flatMap(hotel -> hotelRepository.deleteById(id.toString()));
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
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .flatMap(
            existingHotel -> {
              hotel.setId(id.toString());
              return hotelRepository.save(hotel).then();
            });
  }

  @Override
  public Mono<Void> createHotel(Hotel hotel) {
    if (hotel == null) {
      return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
    }
    try {
      // Validate ID format if present
      if (hotel.getId() != null) {
        Integer.parseInt(hotel.getId());
      }
      return hotelRepository.save(hotel).then();
    } catch (NumberFormatException e) {
      return Mono.error(new IllegalArgumentException("Invalid hotel ID format"));
    }
  }
}
