package com.galaxyviewtower.hotel.crud.controller;

import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public Mono<ResponseEntity<Room>> createRoom(@RequestBody Room room) {
        return roomService.createRoom(room)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Room>> getRoomById(@PathVariable String id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/hotel/{hotelId}")
    public Flux<Room> getRoomsByHotelId(@PathVariable String hotelId) {
        return roomService.getRoomsByHotelId(hotelId);
    }

    @GetMapping("/type/{roomTypeId}")
    public Flux<Room> getRoomsByRoomTypeId(@PathVariable String roomTypeId) {
        return roomService.getRoomsByRoomTypeId(roomTypeId);
    }

    @GetMapping("/status/{status}")
    public Flux<Room> getRoomsByStatus(@PathVariable Room.RoomStatus status) {
        return roomService.getRoomsByStatus(status);
    }

    @GetMapping("/hotel/{hotelId}/status/{status}")
    public Flux<Room> getRoomsByHotelIdAndStatus(@PathVariable String hotelId, @PathVariable Room.RoomStatus status) {
        return roomService.getRoomsByHotelIdAndStatus(hotelId, status);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Room>> updateRoom(@PathVariable String id, @RequestBody Room room) {
        return roomService.updateRoom(id, room)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Room>> updateRoomStatus(@PathVariable String id, @RequestBody Room.RoomStatus status) {
        return roomService.updateRoomStatus(id, status)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRoom(@PathVariable String id) {
        return roomService.deleteRoom(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 