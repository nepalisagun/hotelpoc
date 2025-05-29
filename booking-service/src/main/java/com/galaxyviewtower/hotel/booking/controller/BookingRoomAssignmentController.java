package com.galaxyviewtower.hotel.booking.controller;

import com.galaxyviewtower.hotel.booking.dto.BookingRoomAssignmentDTO;
import com.galaxyviewtower.hotel.booking.service.BookingRoomAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/booking-room-assignments")
@RequiredArgsConstructor
public class BookingRoomAssignmentController {

    private final BookingRoomAssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookingRoomAssignmentDTO> createAssignment(@Valid @RequestBody BookingRoomAssignmentDTO assignmentDTO) {
        return assignmentService.createAssignment(assignmentDTO);
    }

    @GetMapping("/{id}")
    public Mono<BookingRoomAssignmentDTO> getAssignment(@PathVariable String id) {
        return assignmentService.getAssignment(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAssignment(@PathVariable String id) {
        return assignmentService.deleteAssignment(id);
    }

    @GetMapping("/booking/{bookingId}")
    public Flux<BookingRoomAssignmentDTO> getAssignmentsByBookingId(@PathVariable String bookingId) {
        return assignmentService.getAssignmentsByBookingId(bookingId);
    }

    @GetMapping("/room/{roomId}")
    public Flux<BookingRoomAssignmentDTO> getAssignmentsByRoomId(@PathVariable String roomId) {
        return assignmentService.getAssignmentsByRoomId(roomId);
    }

    @DeleteMapping("/booking/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAssignmentsByBookingId(@PathVariable String bookingId) {
        return assignmentService.deleteAssignmentsByBookingId(bookingId);
    }
} 