package com.galaxyviewtower.hotel.crud.service.impl;

import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.model.RoomAvailability;
import com.galaxyviewtower.hotel.crud.repository.RoomAvailabilityRepository;
import com.galaxyviewtower.hotel.crud.repository.RoomRepository;
import com.galaxyviewtower.hotel.crud.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    @Override
    public Mono<Boolean> isRoomTypeAvailable(String hotelId, String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, int numberOfRooms) {
        return getAvailableRoomCount(hotelId, roomTypeId, checkInDate, checkOutDate)
                .map(count -> count >= numberOfRooms);
    }

    @Override
    public Flux<Room> getAvailableRooms(String hotelId, String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findByHotelIdAndRoomTypeId(hotelId, roomTypeId)
                .filterWhen(room -> isRoomAvailable(room.getId(), checkInDate, checkOutDate));
    }

    @Override
    public Mono<Long> getAvailableRoomCount(String hotelId, String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        return getAvailableRooms(hotelId, roomTypeId, checkInDate, checkOutDate).count();
    }

    @Override
    public Mono<Void> markRoomsAsUnavailable(Flux<String> roomIds, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomIds
                .flatMap(roomId -> {
                    RoomAvailability availability = new RoomAvailability();
                    availability.setId(UUID.randomUUID().toString());
                    availability.setRoomId(roomId);
                    availability.setCheckInDate(checkInDate);
                    availability.setCheckOutDate(checkOutDate);
                    availability.setCreatedAt(LocalDateTime.now());
                    availability.setUpdatedAt(LocalDateTime.now());
                    return roomAvailabilityRepository.save(availability);
                })
                .then();
    }

    @Override
    public Mono<Void> markRoomsAsAvailable(Flux<String> roomIds) {
        return roomIds
                .flatMap(roomId -> roomAvailabilityRepository.deleteById(roomId))
                .then();
    }

    private Mono<Boolean> isRoomAvailable(String roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomAvailabilityRepository.countOverlappingBookings(roomId, checkInDate, checkOutDate)
                .map(count -> count == 0);
    }
} 