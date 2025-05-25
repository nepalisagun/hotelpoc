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
    return hotelRepository.findById(id.toString());
  }

  @Override
  public Mono<Void> deleteHotel(Integer id) {
    return hotelRepository.deleteById(id.toString());
  }

  @Override
  public Mono<Void> updateHotel(Integer id, Hotel hotel) {
    hotel.setId(id.toString());
    return hotelRepository.save(hotel).then();
  }

  @Override
  public Mono<Void> createHotel(Hotel hotel) {
    return hotelRepository.save(hotel).then();
  }
}
