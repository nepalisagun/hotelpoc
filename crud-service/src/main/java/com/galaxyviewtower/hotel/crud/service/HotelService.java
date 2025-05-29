package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.model.Hotel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HotelService {
  Flux<Hotel> getAllHotels();

  Mono<Hotel> getHotelById(String id);

  Mono<Void> deleteHotel(String id);

  Mono<Void> updateHotel(String id, Hotel hotel);

  Mono<Void> createHotel(Hotel hotel);

  Flux<Hotel> searchHotels(String city, String country, Double minRating, Double maxPrice);
}
