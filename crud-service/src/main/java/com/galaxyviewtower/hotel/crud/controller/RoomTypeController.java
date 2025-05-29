package com.galaxyviewtower.hotel.crud.controller;

import com.galaxyviewtower.hotel.crud.model.RoomType;
import com.galaxyviewtower.hotel.crud.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    private final RoomTypeService roomTypeService;

    @PostMapping
    public Mono<ResponseEntity<RoomType>> createRoomType(@RequestBody RoomType roomType) {
        return roomTypeService.createRoomType(roomType)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<RoomType> getAllRoomTypes() {
        return roomTypeService.getAllRoomTypes();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<RoomType>> getRoomTypeById(@PathVariable String id) {
        return roomTypeService.getRoomTypeById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/hotel/{hotelId}")
    public Flux<RoomType> getRoomTypesByHotelId(@PathVariable String hotelId) {
        return roomTypeService.getRoomTypesByHotelId(hotelId);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<RoomType>> updateRoomType(@PathVariable String id, @RequestBody RoomType roomType) {
        return roomTypeService.updateRoomType(id, roomType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRoomType(@PathVariable String id) {
        return roomTypeService.deleteRoomType(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 