package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.model.Hotel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface HotelRepository extends ReactiveCrudRepository<Hotel, String> {
    @Query("SELECT * FROM hotels WHERE city = :city AND country = :country AND rating >= :minRating AND price_per_night <= :maxPrice AND is_active = true")
    Flux<Hotel> findByCityAndCountryAndRatingGreaterThanEqualAndPricePerNightLessThanEqual(
            String city, String country, Double minRating, Double maxPrice);
}
