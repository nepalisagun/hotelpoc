package com.galaxyviewtower.hotel.crud.service.impl;

import com.galaxyviewtower.hotel.crud.model.RoomType;
import com.galaxyviewtower.hotel.crud.repository.RoomTypeRepository;
import com.galaxyviewtower.hotel.crud.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    @Override
    public Mono<RoomType> createRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    @Override
    public Mono<RoomType> getRoomTypeById(String id) {
        return roomTypeRepository.findById(id);
    }

    @Override
    public Flux<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    public Flux<RoomType> getRoomTypesByHotelId(String hotelId) {
        return roomTypeRepository.findByHotelId(hotelId);
    }

    @Override
    public Flux<RoomType> getActiveRoomTypesByHotelId(String hotelId) {
        return roomTypeRepository.findByHotelIdAndIsActiveTrue(hotelId);
    }

    @Override
    public Flux<RoomType> getRoomTypesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return roomTypeRepository.findByBasePricePerNightBetween(minPrice, maxPrice);
    }

    @Override
    public Flux<RoomType> getRoomTypesByMinCapacity(Integer minCapacity) {
        return roomTypeRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    @Override
    public Mono<RoomType> updateRoomType(String id, RoomType roomType) {
        return roomTypeRepository.findById(id)
                .flatMap(existingRoomType -> {
                    roomType.setId(id);
                    return roomTypeRepository.save(roomType);
                });
    }

    @Override
    public Mono<Void> deleteRoomType(String id) {
        return roomTypeRepository.deleteById(id);
    }

    @Override
    public Mono<RoomType> toggleRoomTypeStatus(String id) {
        return roomTypeRepository.findById(id)
                .flatMap(roomType -> {
                    roomType.setActive(!roomType.isActive());
                    return roomTypeRepository.save(roomType);
                });
    }
} 