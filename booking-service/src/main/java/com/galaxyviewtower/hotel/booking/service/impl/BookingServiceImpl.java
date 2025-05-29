package com.galaxyviewtower.hotel.booking.service.impl;

import com.galaxyviewtower.hotel.booking.client.CrudServiceClient;
import com.galaxyviewtower.hotel.booking.dto.BookingDTO;
import com.galaxyviewtower.hotel.booking.dto.HotelDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomTypeDTO;
import com.galaxyviewtower.hotel.booking.exception.BookingException;
import com.galaxyviewtower.hotel.booking.mapper.BookingMapper;
import com.galaxyviewtower.hotel.booking.model.Booking;
import com.galaxyviewtower.hotel.booking.repository.BookingRepository;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CrudServiceClient crudServiceClient;

    @Override
    public Mono<BookingDTO> createBooking(BookingDTO bookingDTO) {
        return validateBookingRequest(bookingDTO)
                .flatMap(validatedDTO -> crudServiceClient.getHotel(validatedDTO.getHotelId())
                        .switchIfEmpty(Mono.error(new BookingException("Hotel not found")))
                        .flatMap(hotel -> crudServiceClient.getRoomType(validatedDTO.getRoomTypeId())
                                .switchIfEmpty(Mono.error(new BookingException("Room type not found")))
                                .flatMap(roomType -> validateRoomAvailability(validatedDTO, hotel, roomType)
                                        .flatMap(isAvailable -> {
                                            if (!isAvailable) {
                                                return Mono.error(new BookingException("Room is not available for the selected dates"));
                                            }
                                            return calculateTotalPrice(validatedDTO, roomType)
                                                    .map(price -> {
                                                        Booking booking = bookingMapper.toEntity(validatedDTO);
                                                        booking.setId(UUID.randomUUID().toString());
                                                        booking.setTotalPrice(price);
                                                        booking.setStatus(Booking.BookingStatus.PENDING);
                                                        booking.setBookedAt(LocalDateTime.now());
                                                        booking.setUpdatedAt(LocalDateTime.now());
                                                        return booking;
                                                    });
                                        }))))
                .flatMap(bookingRepository::save)
                .map(bookingMapper::toDTO)
                .doOnSuccess(booking -> log.info("Booking created successfully: {}", booking.getId()))
                .doOnError(error -> log.error("Error creating booking: {}", error.getMessage()));
    }

    @Override
    public Mono<BookingDTO> getBooking(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingException("Booking not found")))
                .map(bookingMapper::toDTO);
    }

    @Override
    public Mono<BookingDTO> updateBooking(String id, BookingDTO bookingDTO) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingException("Booking not found")))
                .flatMap(existingBooking -> {
                    if (existingBooking.getStatus() != Booking.BookingStatus.PENDING) {
                        return Mono.error(new BookingException("Cannot update booking in " + existingBooking.getStatus() + " status"));
                    }
                    bookingMapper.updateEntityFromDTO(bookingDTO, existingBooking);
                    existingBooking.setUpdatedAt(LocalDateTime.now());
                    return bookingRepository.save(existingBooking);
                })
                .map(bookingMapper::toDTO);
    }

    @Override
    public Mono<Void> deleteBooking(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingException("Booking not found")))
                .flatMap(booking -> {
                    if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                        return Mono.error(new BookingException("Cannot delete booking in " + booking.getStatus() + " status"));
                    }
                    return bookingRepository.deleteById(id);
                });
    }

    @Override
    public Flux<BookingDTO> getBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId)
                .map(bookingMapper::toDTO);
    }

    @Override
    public Flux<BookingDTO> getBookingsByHotelId(String hotelId) {
        return bookingRepository.findByHotelId(hotelId)
                .map(bookingMapper::toDTO);
    }

    @Override
    public Mono<Boolean> isRoomAvailable(String roomId, String checkInDate, String checkOutDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        
        return bookingRepository.findRoomBookings(roomId, checkIn, checkOut)
                .hasElements()
                .map(hasBookings -> !hasBookings);
    }

    @Override
    public Mono<BookingDTO> cancelBooking(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingException("Booking not found")))
                .flatMap(booking -> {
                    if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                        return Mono.error(new BookingException("Booking is already cancelled"));
                    }
                    if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
                        return Mono.error(new BookingException("Cannot cancel a completed booking"));
                    }
                    booking.setStatus(Booking.BookingStatus.CANCELLED);
                    booking.setUpdatedAt(LocalDateTime.now());
                    return bookingRepository.save(booking);
                })
                .map(bookingMapper::toDTO);
    }

    @Override
    public Mono<BookingDTO> confirmBooking(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingException("Booking not found")))
                .flatMap(booking -> {
                    if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                        return Mono.error(new BookingException("Only pending bookings can be confirmed"));
                    }
                    booking.setStatus(Booking.BookingStatus.CONFIRMED);
                    booking.setUpdatedAt(LocalDateTime.now());
                    return bookingRepository.save(booking);
                })
                .map(bookingMapper::toDTO);
    }

    private Mono<BookingDTO> validateBookingRequest(BookingDTO bookingDTO) {
        LocalDate checkIn = LocalDate.parse(bookingDTO.getCheckInDate());
        LocalDate checkOut = LocalDate.parse(bookingDTO.getCheckOutDate());
        LocalDate today = LocalDate.now();

        if (checkIn.isBefore(today)) {
            return Mono.error(new BookingException("Check-in date cannot be in the past"));
        }
        if (checkOut.isBefore(checkIn)) {
            return Mono.error(new BookingException("Check-out date must be after check-in date"));
        }
        if (bookingDTO.getNumberOfGuests() <= 0) {
            return Mono.error(new BookingException("Number of guests must be positive"));
        }

        return Mono.just(bookingDTO);
    }

    private Mono<Boolean> validateRoomAvailability(BookingDTO bookingDTO, HotelDTO hotel, RoomTypeDTO roomType) {
        return crudServiceClient.checkRoomAvailability(
                bookingDTO.getHotelId(),
                bookingDTO.getRoomTypeId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate()
        );
    }

    private Mono<Double> calculateTotalPrice(BookingDTO bookingDTO, RoomTypeDTO roomType) {
        LocalDate checkIn = LocalDate.parse(bookingDTO.getCheckInDate());
        LocalDate checkOut = LocalDate.parse(bookingDTO.getCheckOutDate());
        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        return Mono.just(roomType.getPricePerNight().doubleValue() * numberOfNights);
    }
} 