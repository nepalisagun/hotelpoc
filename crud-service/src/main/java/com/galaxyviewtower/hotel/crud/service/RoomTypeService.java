package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.model.RoomType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface RoomTypeService {
    Mono<RoomType> createRoomType(RoomType roomType);
    Mono<RoomType> getRoomTypeById(String id);
    Flux<RoomType> getAllRoomTypes();
    Flux<RoomType> getRoomTypesByHotelId(String hotelId);
    Flux<RoomType> getActiveRoomTypesByHotelId(String hotelId);
    Flux<RoomType> getRoomTypesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    Flux<RoomType> getRoomTypesByMinCapacity(Integer minCapacity);
    Mono<RoomType> updateRoomType(String id, RoomType roomType);
    Mono<Void> deleteRoomType(String id);
    Mono<RoomType> toggleRoomTypeStatus(String id);
} 