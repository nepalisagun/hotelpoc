package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.model.RoomType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

public interface RoomTypeRepository extends R2dbcRepository<RoomType, String> {
    Flux<RoomType> findByHotelId(String hotelId);
    Flux<RoomType> findByHotelIdAndIsActiveTrue(String hotelId);
    Flux<RoomType> findByBasePricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);
    Flux<RoomType> findByCapacityGreaterThanEqual(Integer minCapacity);
} 