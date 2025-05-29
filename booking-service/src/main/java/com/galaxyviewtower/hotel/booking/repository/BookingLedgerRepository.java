package com.galaxyviewtower.hotel.booking.repository;

import com.galaxyviewtower.hotel.booking.model.BookingLedger;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BookingLedgerRepository extends ReactiveCrudRepository<BookingLedger, String> {
    
    @Query("SELECT * FROM booking_ledger WHERE hotel_id = :hotelId AND room_type_id = :roomTypeId " +
           "AND status = 'CONFIRMED' AND " +
           "((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Flux<BookingLedger> findOverlappingBookings(String hotelId, String roomTypeId, 
                                              LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT COUNT(*) FROM booking_ledger WHERE hotel_id = :hotelId AND room_type_id = :roomTypeId " +
           "AND status = 'CONFIRMED' AND " +
           "((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Mono<Long> countOverlappingBookings(String hotelId, String roomTypeId, 
                                      LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT * FROM booking_ledger WHERE booking_id = :bookingId")
    Flux<BookingLedger> findByBookingId(String bookingId);

    @Query("SELECT * FROM booking_ledger WHERE room_id = :roomId AND status = 'CONFIRMED' AND " +
           "((check_in_date <= :checkOutDate AND check_out_date >= :checkInDate))")
    Flux<BookingLedger> findRoomBookings(String roomId, LocalDate checkInDate, LocalDate checkOutDate);
} 