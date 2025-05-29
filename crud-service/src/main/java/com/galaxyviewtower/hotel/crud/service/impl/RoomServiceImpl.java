package com.galaxyviewtower.hotel.crud.service.impl;

import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.repository.RoomRepository;
import com.galaxyviewtower.hotel.crud.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Override
    public Mono<Room> createRoom(Room room) {
        room.setId(UUID.randomUUID().toString());
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        return roomRepository.save(room);
    }

    @Override
    public Flux<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Mono<Room> getRoomById(String id) {
        return roomRepository.findById(id);
    }

    @Override
    public Flux<Room> getRoomsByHotelId(String hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    @Override
    public Flux<Room> getRoomsByRoomTypeId(String roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId);
    }

    @Override
    public Flux<Room> getRoomsByStatus(Room.RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    @Override
    public Flux<Room> getRoomsByHotelIdAndStatus(String hotelId, Room.RoomStatus status) {
        return roomRepository.findByHotelIdAndStatus(hotelId, status);
    }

    @Override
    public Mono<Room> updateRoom(String id, Room room) {
        return roomRepository.findById(id)
                .flatMap(existingRoom -> {
                    room.setId(id);
                    room.setCreatedAt(existingRoom.getCreatedAt());
                    room.setUpdatedAt(LocalDateTime.now());
                    return roomRepository.save(room);
                });
    }

    @Override
    public Mono<Room> updateRoomStatus(String id, Room.RoomStatus status) {
        return roomRepository.findById(id)
                .flatMap(room -> {
                    room.setStatus(status);
                    room.setUpdatedAt(LocalDateTime.now());
                    return roomRepository.save(room);
                });
    }

    @Override
    public Mono<Void> deleteRoom(String id) {
        return roomRepository.deleteById(id);
    }
} 