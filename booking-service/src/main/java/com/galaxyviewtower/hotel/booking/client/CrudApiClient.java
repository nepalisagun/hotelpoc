package com.galaxyviewtower.hotel.booking.client;

import reactor.core.publisher.Mono;

public interface CrudApiClient {
  Mono<Object> getHotelById(String hotelId);
}
