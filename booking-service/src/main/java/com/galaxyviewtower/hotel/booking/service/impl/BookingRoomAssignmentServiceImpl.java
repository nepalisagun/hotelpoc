package com.galaxyviewtower.hotel.booking.service.impl;

import com.galaxyviewtower.hotel.booking.dto.BookingRoomAssignmentDTO;
import com.galaxyviewtower.hotel.booking.mapper.BookingRoomAssignmentMapper;
import com.galaxyviewtower.hotel.booking.model.BookingRoomAssignment;
import com.galaxyviewtower.hotel.booking.repository.BookingRoomAssignmentRepository;
import com.galaxyviewtower.hotel.booking.service.BookingRoomAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingRoomAssignmentServiceImpl implements BookingRoomAssignmentService {

    private final BookingRoomAssignmentRepository assignmentRepository;
    private final BookingRoomAssignmentMapper assignmentMapper;

    @Override
    public Mono<BookingRoomAssignmentDTO> createAssignment(BookingRoomAssignmentDTO assignmentDTO) {
        BookingRoomAssignment assignment = assignmentMapper.toEntity(assignmentDTO);
        assignment.setId(UUID.randomUUID().toString());
        assignment.setAssignedDate(LocalDateTime.now());
        
        return assignmentRepository.save(assignment)
                .map(assignmentMapper::toDTO);
    }

    @Override
    public Mono<BookingRoomAssignmentDTO> getAssignment(String id) {
        return assignmentRepository.findById(id)
                .map(assignmentMapper::toDTO);
    }

    @Override
    public Mono<Void> deleteAssignment(String id) {
        return assignmentRepository.deleteById(id);
    }

    @Override
    public Flux<BookingRoomAssignmentDTO> getAssignmentsByBookingId(String bookingId) {
        return assignmentRepository.findByBookingId(bookingId)
                .map(assignmentMapper::toDTO);
    }

    @Override
    public Flux<BookingRoomAssignmentDTO> getAssignmentsByRoomId(String roomId) {
        return assignmentRepository.findByRoomId(roomId)
                .map(assignmentMapper::toDTO);
    }

    @Override
    public Mono<Void> deleteAssignmentsByBookingId(String bookingId) {
        return assignmentRepository.deleteByBookingId(bookingId);
    }
} 