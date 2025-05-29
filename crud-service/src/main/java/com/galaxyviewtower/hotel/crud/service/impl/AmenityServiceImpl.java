package com.galaxyviewtower.hotel.crud.service.impl;

import com.galaxyviewtower.hotel.crud.model.Amenity;
import com.galaxyviewtower.hotel.crud.repository.AmenityRepository;
import com.galaxyviewtower.hotel.crud.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;

    @Override
    public Mono<Amenity> createAmenity(Amenity amenity) {
        amenity.setId(UUID.randomUUID().toString());
        amenity.setCreatedAt(LocalDateTime.now());
        amenity.setUpdatedAt(LocalDateTime.now());
        return amenityRepository.save(amenity);
    }

    @Override
    public Flux<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    @Override
    public Mono<Amenity> getAmenityById(String id) {
        return amenityRepository.findById(id);
    }

    @Override
    public Flux<Amenity> getAmenitiesByCategory(String category) {
        return amenityRepository.findByCategory(category);
    }

    @Override
    public Mono<Amenity> updateAmenity(String id, Amenity amenity) {
        return amenityRepository.findById(id)
                .flatMap(existingAmenity -> {
                    amenity.setId(id);
                    amenity.setCreatedAt(existingAmenity.getCreatedAt());
                    amenity.setUpdatedAt(LocalDateTime.now());
                    return amenityRepository.save(amenity);
                });
    }

    @Override
    public Mono<Void> deleteAmenity(String id) {
        return amenityRepository.deleteById(id);
    }

    @Override
    public Mono<Amenity> toggleAmenityStatus(String id) {
        return amenityRepository.findById(id)
                .flatMap(amenity -> {
                    amenity.setActive(!amenity.isActive());
                    return amenityRepository.save(amenity);
                });
    }
} 