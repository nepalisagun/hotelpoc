package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.model.Hotel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HotelService {
  Flux<Hotel> getAllHotels();

  Mono<Hotel> getHotelById(Integer id);

  Mono<Void> deleteHotel(Integer id);

  Mono<Void> updateHotel(Integer id, Hotel hotel);

  Mono<Void> createHotel(Hotel hotel);
}
