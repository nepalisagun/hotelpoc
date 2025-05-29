package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.Hotel;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import com.galaxyviewtower.hotel.crud.exception.HotelNotFoundException;
import com.galaxyviewtower.hotel.crud.exception.InvalidHotelDataException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final Validator validator;
    private final TransactionalOperator transactionalOperator;

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final int BUFFER_SIZE = 100;

    @Override
    public Flux<Hotel> getAllHotels() {
        return hotelRepository.findAll()
                .timeout(TIMEOUT)
                .onBackpressureBuffer(BUFFER_SIZE)
                .publishOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Error fetching hotels: {}", e.getMessage()));
    }

    @Override
    public Mono<Hotel> getHotelById(String id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
        }
        return hotelRepository.findById(id)
                .timeout(TIMEOUT)
                .publishOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new HotelNotFoundException("Hotel not found with id: " + id)))
                .doOnError(e -> log.error("Error fetching hotel {}: {}", id, e.getMessage()));
    }

    @Override
    public Mono<Void> deleteHotel(String id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
        }
        return hotelRepository.findById(id)
                .timeout(TIMEOUT)
                .switchIfEmpty(Mono.error(new HotelNotFoundException("Hotel not found with id: " + id)))
                .flatMap(hotel -> {
                    hotel.setActive(false);
                    return hotelRepository.save(hotel);
                })
                .publishOn(Schedulers.boundedElastic())
                .then()
                .doOnError(e -> log.error("Error soft-deleting hotel {}: {}", id, e.getMessage()));
    }

    @Override
    public Mono<Void> updateHotel(String id, Hotel hotel) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("Hotel ID cannot be null"));
        }
        if (hotel == null) {
            return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
        }

        // Validate hotel data
        Set<jakarta.validation.ConstraintViolation<Hotel>> violations = validator.validate(hotel);
        if (!violations.isEmpty()) {
            return Mono.error(new InvalidHotelDataException("Invalid hotel data: " + violations));
        }

        return hotelRepository.findById(id)
                .timeout(TIMEOUT)
                .switchIfEmpty(Mono.error(new HotelNotFoundException("Hotel not found with id: " + id)))
                .flatMap(existingHotel -> {
                    hotel.setId(id);
                    return hotelRepository.save(hotel);
                })
                .publishOn(Schedulers.boundedElastic())
                .then()
                .doOnError(e -> log.error("Error updating hotel {}: {}", id, e.getMessage()));
    }

    @Override
    public Mono<Void> createHotel(Hotel hotel) {
        if (hotel == null) {
            log.error("Hotel cannot be null");
            return Mono.error(new IllegalArgumentException("Hotel cannot be null"));
        }

        // Validate hotel data
        Set<jakarta.validation.ConstraintViolation<Hotel>> violations = validator.validate(hotel);
        if (!violations.isEmpty()) {
            return Mono.error(new InvalidHotelDataException("Invalid hotel data: " + violations));
        }

        // Generate UUID for new hotel
        hotel.setId(java.util.UUID.randomUUID().toString());
        hotel.setActive(true);

        return hotelRepository.save(hotel)
                .timeout(TIMEOUT)
                .publishOn(Schedulers.boundedElastic())
                .then()
                .doOnError(e -> log.error("Error creating hotel: {} | Details: {}", e.getMessage(), e));
    }

    @Override
    public Flux<Hotel> searchHotels(String city, String country, Double minRating, Double maxPrice) {
        return hotelRepository.findByCityAndCountryAndRatingGreaterThanEqualAndPricePerNightLessThanEqual(
                city, country, minRating, maxPrice)
                .timeout(TIMEOUT)
                .onBackpressureBuffer(BUFFER_SIZE)
                .publishOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Error searching hotels: {}", e.getMessage()));
    }
}
