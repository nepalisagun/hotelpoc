package com.galaxyviewtower.hotel.booking.service;

import com.galaxyviewtower.hotel.booking.dto.BookingRoomAssignmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRoomAssignmentService {
    Mono<BookingRoomAssignmentDTO> createAssignment(BookingRoomAssignmentDTO assignmentDTO);
    Mono<BookingRoomAssignmentDTO> getAssignment(String id);
    Mono<Void> deleteAssignment(String id);
    Flux<BookingRoomAssignmentDTO> getAssignmentsByBookingId(String bookingId);
    Flux<BookingRoomAssignmentDTO> getAssignmentsByRoomId(String roomId);
    Mono<Void> deleteAssignmentsByBookingId(String bookingId);
} 