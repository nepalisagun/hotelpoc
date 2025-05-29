package com.galaxyviewtower.hotel.booking.repository;

import com.galaxyviewtower.hotel.booking.model.Booking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {
    
    @Query("SELECT * FROM bookings WHERE user_id = :userId ORDER BY booked_at DESC")
    Flux<Booking> findByUserId(String userId);
    
    @Query("SELECT * FROM bookings WHERE hotel_id = :hotelId ORDER BY booked_at DESC")
    Flux<Booking> findByHotelId(String hotelId);
    
    @Query("SELECT * FROM bookings WHERE room_type_id = :roomTypeId AND status = 'CONFIRMED' " +
           "AND ((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Flux<Booking> findOverlappingBookings(String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);
    
    @Query("SELECT COUNT(*) FROM bookings WHERE room_type_id = :roomTypeId AND status = 'CONFIRMED' " +
           "AND ((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Mono<Long> countOverlappingBookings(String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);
    
    @Query("SELECT * FROM bookings WHERE room_id = :roomId AND status = 'CONFIRMED' " +
           "AND ((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Flux<Booking> findRoomBookings(String roomId, LocalDate checkInDate, LocalDate checkOutDate);
} 