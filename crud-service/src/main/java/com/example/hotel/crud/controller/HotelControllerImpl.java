package com.example.hotel.crud.controller;

import com.example.hotel.crud.api.DefaultApi;
import com.example.hotel.crud.model.gen.HotelGen;
import com.example.hotel.crud.service.HotelService;
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

  @Override
  public Mono<ResponseEntity<Flux<HotelGen>>> hotelsGet(ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsGet request");
    Flux<HotelGen> hotels = hotelService.getAllHotels();
    return Mono.just(ResponseEntity.ok(hotels));
  }

  @Override
  public Mono<ResponseEntity<HotelGen>> hotelsIdGet(Integer id, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsIdGet request for ID: {}", id);
    return hotelService
        .getHotelById(id)
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
      Integer id, Mono<HotelGen> hotelGen, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsIdPut request for ID: {}", id);
    return hotelGen
        .flatMap(hg -> hotelService.updateHotel(id, hg))
        .then(Mono.just(ResponseEntity.ok().<Void>build()))
        .doOnError(e -> log.error("CRUD: Error updating hotel {}: {}", id, e.getMessage()));
  }

  @Override
  public Mono<ResponseEntity<Void>> hotelsPost(
      Mono<HotelGen> hotelGen, ServerWebExchange exchange) {
    log.info("CRUD: Received hotelsPost request");
    return hotelGen
        .flatMap(hotelService::createHotel)
        .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).<Void>build()))
        .doOnError(e -> log.error("CRUD: Error creating hotel: {}", e.getMessage()));
  }
}
