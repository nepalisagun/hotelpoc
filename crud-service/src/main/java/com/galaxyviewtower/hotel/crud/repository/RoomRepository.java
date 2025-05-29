package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.model.Room;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface RoomRepository extends R2dbcRepository<Room, String> {
    Flux<Room> findByHotelId(String hotelId);
    Flux<Room> findByRoomTypeId(String roomTypeId);
    Flux<Room> findByHotelIdAndStatus(String hotelId, String status);
    Flux<Room> findByHotelIdAndIsActiveTrue(String hotelId);
    Flux<Room> findByHotelIdAndRoomTypeIdAndStatus(String hotelId, String roomTypeId, String status);
} 