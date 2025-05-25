package com.galaxyviewtower.hotel.crud.controller;

import com.galaxyviewtower.hotel.crud.api.DefaultApi;
import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.gen.Hotel;
import com.galaxyviewtower.hotel.crud.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HotelControllerImpl implements DefaultApi {

  private final HotelService hotelService;
  private final HotelMapper hotelMapper;

  @Override
  public Mono<ResponseEntity<Flux<Hotel>>> hotelsGet(ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsGet request");
    Flux<Hotel> hotels = hotelService.getAllHotels().map(hotelMapper::toDto);
    return Mono.just(ResponseEntity.ok(hotels));
  }

  @Override
  public Mono<ResponseEntity<Hotel>> hotelsIdGet(Integer id, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsIdGet request for ID: {}", id);
    return hotelService
        .getHotelById(id)
        .map(hotelMapper::toDto)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build())
        .doOnError(e -> log.error("CRUD: Error getting hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<ResponseEntity<Void>> hotelsIdDelete(Integer id, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsIdDelete request for ID: {}", id);
    return hotelService
        .deleteHotel(id)
        .then(Mono.just(ResponseEntity.noContent().<Void>build()))
        .doOnError(e -> log.error("CRUD: Error deleting hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<ResponseEntity<Void>> hotelsIdPut(
      Integer id, Mono<Hotel> hotel, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsIdPut request for ID: {}", id);
    return hotel
        .map(hotelMapper::toEntity)
        .flatMap(h -> hotelService.updateHotel(id, h))
        .then(Mono.just(ResponseEntity.ok().<Void>build()))
        .doOnError(e -> log.error("CRUD: Error updating hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<ResponseEntity<Void>> hotelsPost(Mono<Hotel> hotel, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsPost request");
    return hotel
        .map(hotelMapper::toEntity)
        .flatMap(h -> hotelService.createHotel(h))
        .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).<Void>build()))
        .doOnError(e -> log.error("CRUD: Error creating hotel: {}", e.getMessage()));
  }
}
