package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.model.Amenity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AmenityService {
    Mono<Amenity> createAmenity(Amenity amenity);
    Mono<Amenity> getAmenityById(String id);
    Flux<Amenity> getAllAmenities();
    Flux<Amenity> getActiveAmenities();
    Flux<Amenity> searchAmenitiesByName(String name);
    Mono<Amenity> updateAmenity(String id, Amenity amenity);
    Mono<Void> deleteAmenity(String id);
    Mono<Amenity> toggleAmenityStatus(String id);
} 