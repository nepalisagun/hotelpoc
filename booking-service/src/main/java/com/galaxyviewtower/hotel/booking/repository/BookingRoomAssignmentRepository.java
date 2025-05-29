package com.galaxyviewtower.hotel.booking.repository;

import com.galaxyviewtower.hotel.booking.model.BookingRoomAssignment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BookingRoomAssignmentRepository extends ReactiveCrudRepository<BookingRoomAssignment, String> {
    
    @Query("SELECT * FROM booking_room_assignments WHERE booking_id = :bookingId")
    Flux<BookingRoomAssignment> findByBookingId(String bookingId);
    
    @Query("SELECT * FROM booking_room_assignments WHERE room_id = :roomId")
    Flux<BookingRoomAssignment> findByRoomId(String roomId);
    
    @Query("DELETE FROM booking_room_assignments WHERE booking_id = :bookingId")
    Flux<Void> deleteByBookingId(String bookingId);
} 