package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.model.RoomAvailability;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface RoomAvailabilityRepository extends ReactiveCrudRepository<RoomAvailability, String> {
    
    @Query("SELECT * FROM room_availability WHERE room_id = :roomId AND " +
           "((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Flux<RoomAvailability> findOverlappingBookings(String roomId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT COUNT(*) FROM room_availability WHERE room_id = :roomId AND " +
           "((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Mono<Long> countOverlappingBookings(String roomId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT * FROM room_availability WHERE booking_id = :bookingId")
    Flux<RoomAvailability> findByBookingId(String bookingId);

    @Query("DELETE FROM room_availability WHERE booking_id = :bookingId")
    Mono<Void> deleteByBookingId(String bookingId);
} 