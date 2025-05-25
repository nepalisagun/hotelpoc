package com.example.hotel.crud.service;

import com.example.hotel.crud.model.gen.HotelGen;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HotelService {
  Flux<HotelGen> getAllHotels();

  Mono<HotelGen> getHotelById(Integer id);

  Mono<Void> deleteHotel(Integer id);

  Mono<Void> updateHotel(Integer id, HotelGen hotelGen);

  Mono<Void> createHotel(HotelGen hotelGen);
}
