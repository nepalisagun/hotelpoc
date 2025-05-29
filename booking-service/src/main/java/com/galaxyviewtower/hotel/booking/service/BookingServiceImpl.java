package com.galaxyviewtower.hotel.booking.service;

import com.galaxyviewtower.hotel.booking.dto.CreateBookingDto;
import com.galaxyviewtower.hotel.booking.dto.BookingResponseDto;
import com.galaxyviewtower.hotel.booking.model.*;
import com.galaxyviewtower.hotel.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CrudServiceClient crudServiceClient;

    @Override
    public Mono<BookingResponseDto> createBooking(CreateBookingDto request) {
        log.info("Received booking request - User: {}, Hotel: {}, Check-in: {}, Check-out: {}, Rooms: {}", 
            request.getUserId(), request.getHotelId(), request.getCheckIn(), request.getCheckOut(), request.getRooms());
        
        // Validate dates first
        if (request.getCheckIn().isAfter(request.getCheckOut())) {
            return Mono.error(new IllegalArgumentException("Check-in date must be before check-out date"));
        }
        
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            return Mono.error(new IllegalArgumentException("Check-in date must be in the future"));
        }

        return crudServiceClient.getHotelById(request.getHotelId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Hotel not found with id: " + request.getHotelId())))
                .doOnNext(hotel -> log.info("Retrieved hotel details - Hotel ID: {}, Name: {}, Total Rooms: {}, Price per night: {}", 
                    hotel.getId(), hotel.getName(), hotel.getTotalRooms(), hotel.getPricePerNight()))
                .flatMap(hotel -> {
                    // Check room availability
                    return bookingRepository.findByHotelIdAndCheckInBetween(
                            request.getHotelId(), 
                            request.getCheckIn(), 
                            request.getCheckOut())
                            .collectList()
                            .flatMap(existingBookings -> {
                                int bookedRooms = existingBookings.stream()
                                        .mapToInt(Booking::getRooms)
                                        .sum();
                                
                                if (hotel.getTotalRooms() - bookedRooms < request.getRooms()) {
                                    return Mono.error(new IllegalStateException(
                                        String.format("Not enough rooms available. Requested: %d, Available: %d", 
                                            request.getRooms(), 
                                            hotel.getTotalRooms() - bookedRooms)));
                                }

                                // Create new booking
                                Booking booking = new Booking();
                                booking.setId(UUID.randomUUID().toString());
                                booking.setUserId(request.getUserId());
                                booking.setHotelId(request.getHotelId());
                                booking.setCheckIn(request.getCheckIn());
                                booking.setCheckOut(request.getCheckOut());
                                booking.setRooms(request.getRooms());
                                booking.setTotalPrice(calculateTotalPrice(hotel, request));
                                booking.setStatus(BookingStatus.PENDING);
                                booking.setSpecialRequests(request.getSpecialRequests());
                                booking.setCreatedAt(LocalDateTime.now());
                                booking.setUpdatedAt(LocalDateTime.now());

                                log.info("Saving booking to database - Booking ID: {}, Total Price: {}", booking.getId(), booking.getTotalPrice());
                                
                                return bookingRepository.save(booking)
                                        .map(savedBooking -> BookingResponseDto.builder()
                                                .bookingId(savedBooking.getId())
                                                .userId(savedBooking.getUserId())
                                                .hotelId(savedBooking.getHotelId())
                                                .checkIn(savedBooking.getCheckIn())
                                                .checkOut(savedBooking.getCheckOut())
                                                .rooms(savedBooking.getRooms())
                                                .totalPrice(savedBooking.getTotalPrice())
                                                .status(savedBooking.getStatus())
                                                .message("Booking created successfully")
                                                .createdAt(savedBooking.getCreatedAt())
                                                .build())
                                        .doOnSuccess(response -> log.info("Booking creation completed successfully - Booking ID: {}", response.getBookingId()));
                            });
                })
                .onErrorResume(e -> {
                    log.error("Error creating booking: {} | Stack trace: {}", e.getMessage(), e);
                    if (e instanceof IllegalArgumentException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new RuntimeException("Failed to create booking: " + e.getMessage()));
                });
    }

    @Override
    public Mono<BookingResponseDto> getBooking(String bookingId) {
        log.info("Retrieving booking with ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .map(booking -> BookingResponseDto.builder()
                        .bookingId(booking.getId())
                        .userId(booking.getUserId())
                        .hotelId(booking.getHotelId())
                        .checkIn(booking.getCheckIn())
                        .checkOut(booking.getCheckOut())
                        .rooms(booking.getRooms())
                        .totalPrice(booking.getTotalPrice())
                        .status(booking.getStatus())
                        .message("Booking retrieved successfully")
                        .createdAt(booking.getCreatedAt())
                        .build())
                .doOnError(e -> log.error("Error retrieving booking {}: {}", bookingId, e.getMessage()));
    }

    @Override
    public Flux<BookingResponseDto> getUserBookings(String userId) {
        log.info("Retrieving bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId)
                .map(booking -> BookingResponseDto.builder()
                        .bookingId(booking.getId())
                        .userId(booking.getUserId())
                        .hotelId(booking.getHotelId())
                        .checkIn(booking.getCheckIn())
                        .checkOut(booking.getCheckOut())
                        .rooms(booking.getRooms())
                        .totalPrice(booking.getTotalPrice())
                        .status(booking.getStatus())
                        .message("Booking retrieved successfully")
                        .createdAt(booking.getCreatedAt())
                        .build())
                .doOnError(e -> log.error("Error retrieving bookings for user {}: {}", userId, e.getMessage()));
    }

    @Override
    public Flux<BookingResponseDto> getHotelBookings(String hotelId, LocalDate startDate, LocalDate endDate) {
        log.info("Retrieving bookings for hotel {} between {} and {}", hotelId, startDate, endDate);
        return bookingRepository.findByHotelIdAndCheckInBetween(hotelId, startDate, endDate)
                .map(booking -> BookingResponseDto.builder()
                        .bookingId(booking.getId())
                        .userId(booking.getUserId())
                        .hotelId(booking.getHotelId())
                        .checkIn(booking.getCheckIn())
                        .checkOut(booking.getCheckOut())
                        .rooms(booking.getRooms())
                        .totalPrice(booking.getTotalPrice())
                        .status(booking.getStatus())
                        .message("Booking retrieved successfully")
                        .createdAt(booking.getCreatedAt())
                        .build())
                .doOnError(e -> log.error("Error retrieving bookings for hotel {}: {}", hotelId, e.getMessage()));
    }

    @Override
    public Mono<BookingResponseDto> cancelBooking(String bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setUpdatedAt(LocalDateTime.now());
                    return bookingRepository.save(booking)
                            .map(savedBooking -> BookingResponseDto.builder()
                                    .bookingId(savedBooking.getId())
                                    .userId(savedBooking.getUserId())
                                    .hotelId(savedBooking.getHotelId())
                                    .checkIn(savedBooking.getCheckIn())
                                    .checkOut(savedBooking.getCheckOut())
                                    .rooms(savedBooking.getRooms())
                                    .totalPrice(savedBooking.getTotalPrice())
                                    .status(savedBooking.getStatus())
                                    .message("Booking cancelled successfully")
                                    .createdAt(savedBooking.getCreatedAt())
                                    .build());
                })
                .doOnError(e -> log.error("Error cancelling booking {}: {}", bookingId, e.getMessage()));
    }

    @Override
    public Mono<BookingResponseDto> modifyBooking(String bookingId, CreateBookingDto request) {
        log.info("Modifying booking with ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .flatMap(booking -> crudServiceClient.getHotelById(request.getHotelId())
                        .flatMap(hotel -> {
                            booking.setCheckIn(request.getCheckIn());
                            booking.setCheckOut(request.getCheckOut());
                            booking.setRooms(request.getRooms());
                            booking.setSpecialRequests(request.getSpecialRequests());
                            booking.setUpdatedAt(LocalDateTime.now());
                            booking.setTotalPrice(calculateTotalPrice(hotel, request));

                            return bookingRepository.save(booking)
                                    .map(savedBooking -> BookingResponseDto.builder()
                                            .bookingId(savedBooking.getId())
                                            .userId(savedBooking.getUserId())
                                            .hotelId(savedBooking.getHotelId())
                                            .checkIn(savedBooking.getCheckIn())
                                            .checkOut(savedBooking.getCheckOut())
                                            .rooms(savedBooking.getRooms())
                                            .totalPrice(savedBooking.getTotalPrice())
                                            .status(savedBooking.getStatus())
                                            .message("Booking modified successfully")
                                            .createdAt(savedBooking.getCreatedAt())
                                            .build());
                        }))
                .doOnError(e -> log.error("Error modifying booking {}: {}", bookingId, e.getMessage()));
    }

    @Override
    public Mono<Boolean> checkAvailability(String hotelId, String checkIn, String checkOut, int rooms) {
        log.info("Checking availability for hotel {} between {} and {} for {} rooms", hotelId, checkIn, checkOut, rooms);
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);
        
        return crudServiceClient.getHotelById(hotelId)
                .flatMap(hotel -> bookingRepository.findByHotelIdAndCheckInBetween(hotelId, checkInDate, checkOutDate)
                        .collectList()
                        .map(bookings -> {
                            int bookedRooms = bookings.stream()
                                    .mapToInt(Booking::getRooms)
                                    .sum();
                            return hotel.getTotalRooms() - bookedRooms >= rooms;
                        }))
                .doOnError(e -> log.error("Error checking availability for hotel {}: {}", hotelId, e.getMessage()));
    }

    private double calculateTotalPrice(Hotel hotel, CreateBookingDto request) {
        long days = request.getCheckOut().toEpochDay() - request.getCheckIn().toEpochDay();
        return hotel.getPricePerNight() * request.getRooms() * days;
    }
} 