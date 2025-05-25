package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.Hotel;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@Observed(name = "hotel.service", contextualName = "hotel-operations")
public class HotelServiceImpl implements HotelService {
  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

  @Override
  @Observed(name = "get.all.hotels", contextualName = "get-all-hotels")
  public Flux<Hotel> getAllHotels() {
    return hotelRepository
        .findAll()
        .doOnSubscribe(s -> log.debug("Fetching all hotels"))
        .doOnComplete(() -> log.debug("Successfully fetched all hotels"));
  }

  @Override
  @Observed(name = "get.hotel.by.id", contextualName = "get-hotel-by-id")
  public Mono<Hotel> getHotelById(Integer id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .doOnSubscribe(s -> log.debug("Fetching hotel with id: {}", id))
        .doOnSuccess(hotel -> log.debug("Successfully fetched hotel with id: {}", id))
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)));
  }

  @Override
  @Observed(name = "delete.hotel", contextualName = "delete-hotel")
  public Mono<Void> deleteHotel(Integer id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .doOnSubscribe(s -> log.debug("Attempting to delete hotel with id: {}", id))
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .flatMap(
            hotel ->
                hotelRepository
                    .deleteById(id.toString())
                    .doOnSuccess(v -> log.debug("Successfully deleted hotel with id: {}", id)));
  }

  @Override
  @Observed(name = "update.hotel", contextualName = "update-hotel")
  public Mono<Void> updateHotel(Integer id, Hotel hotel) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
    }
    if (hotel == null) {
      return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
    }
    return hotelRepository
        .findById(id.toString())
        .doOnSubscribe(s -> log.debug("Attempting to update hotel with id: {}", id))
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + id)))
        .flatMap(
            existingHotel -> {
              hotel.setId(id.toString());
              return hotelRepository
                  .save(hotel)
                  .doOnSuccess(h -> log.debug("Successfully updated hotel with id: {}", id))
                  .then();
            });
  }

  @Override
  @Observed(name = "create.hotel", contextualName = "create-hotel")
  public Mono<Void> createHotel(Hotel hotel) {
    if (hotel == null) {
      return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
    }
    try {
      // Validate ID format if present
      if (hotel.getId() != null) {
        Integer.parseInt(hotel.getId());
      }
      return hotelRepository
          .save(hotel)
          .doOnSubscribe(s -> log.debug("Creating new hotel"))
          .doOnSuccess(h -> log.debug("Successfully created hotel with id: {}", h.getId()))
          .then();
    } catch (NumberFormatException e) {
      return Mono.error(new IllegalArgumentException("Invalid hotel ID format"));
    }
  }
}
