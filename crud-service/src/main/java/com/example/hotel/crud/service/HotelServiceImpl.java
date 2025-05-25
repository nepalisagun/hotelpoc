package com.example.hotel.crud.service;

import com.example.hotel.crud.mapper.HotelMapper;
import com.example.hotel.crud.model.gen.HotelGen;
import com.example.hotel.crud.repository.HotelRepository;
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
  public Flux<HotelGen> getAllHotels() {
    return hotelRepository.findAll().map(hotelMapper::toDto);
  }

  @Override
  public Mono<HotelGen> getHotelById(Integer id) {
    return hotelRepository.findById(id.toString()).map(hotelMapper::toDto);
  }

  @Override
  public Mono<Void> deleteHotel(Integer id) {
    return hotelRepository.deleteById(id.toString());
  }

  @Override
  public Mono<Void> updateHotel(Integer id, HotelGen hotelGen) {
    return Mono.just(hotelGen)
        .map(hotelMapper::toEntity)
        .flatMap(
            hotel -> {
              hotel.setId(id.toString());
              return hotelRepository.save(hotel);
            })
        .then();
  }

  @Override
  public Mono<Void> createHotel(HotelGen hotelGen) {
    return Mono.just(hotelGen).map(hotelMapper::toEntity).flatMap(hotelRepository::save).then();
  }
}
