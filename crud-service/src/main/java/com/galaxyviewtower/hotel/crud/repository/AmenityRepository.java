package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.model.Amenity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface AmenityRepository extends R2dbcRepository<Amenity, String> {
    Flux<Amenity> findByIsActiveTrue();
    Flux<Amenity> findByNameContainingIgnoreCase(String name);
} 