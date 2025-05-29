package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.model.Room;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoomService {
    Mono<Room> createRoom(Room room);
    Mono<Room> getRoomById(String id);
    Flux<Room> getAllRooms();
    Flux<Room> getRoomsByHotelId(String hotelId);
    Flux<Room> getRoomsByRoomTypeId(String roomTypeId);
    Flux<Room> getRoomsByHotelIdAndStatus(String hotelId, String status);
    Flux<Room> getActiveRoomsByHotelId(String hotelId);
    Flux<Room> getAvailableRoomsByHotelIdAndRoomTypeId(String hotelId, String roomTypeId);
    Mono<Room> updateRoom(String id, Room room);
    Mono<Void> deleteRoom(String id);
    Mono<Room> updateRoomStatus(String id, String status);
    Mono<Room> toggleRoomStatus(String id);
} 