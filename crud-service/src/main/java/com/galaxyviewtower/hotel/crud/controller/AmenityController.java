package com.galaxyviewtower.hotel.crud.controller;

import com.galaxyviewtower.hotel.crud.model.Amenity;
import com.galaxyviewtower.hotel.crud.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping
    public Mono<ResponseEntity<Amenity>> createAmenity(@RequestBody Amenity amenity) {
        return amenityService.createAmenity(amenity)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Amenity> getAllAmenities() {
        return amenityService.getAllAmenities();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Amenity>> getAmenityById(@PathVariable String id) {
        return amenityService.getAmenityById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public Flux<Amenity> getAmenitiesByCategory(@PathVariable String category) {
        return amenityService.getAmenitiesByCategory(category);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Amenity>> updateAmenity(@PathVariable String id, @RequestBody Amenity amenity) {
        return amenityService.updateAmenity(id, amenity)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAmenity(@PathVariable String id) {
        return amenityService.deleteAmenity(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 