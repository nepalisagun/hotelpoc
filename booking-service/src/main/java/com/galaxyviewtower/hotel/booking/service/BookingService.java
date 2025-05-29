package com.galaxyviewtower.hotel.booking.service;

import com.galaxyviewtower.hotel.booking.dto.BookingDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {
    Mono<BookingDTO> createBooking(BookingDTO bookingDTO);
    Mono<BookingDTO> getBooking(String id);
    Mono<BookingDTO> updateBooking(String id, BookingDTO bookingDTO);
    Mono<Void> deleteBooking(String id);
    Flux<BookingDTO> getBookingsByUserId(String userId);
    Flux<BookingDTO> getBookingsByHotelId(String hotelId);
    Mono<Boolean> isRoomAvailable(String roomId, String checkInDate, String checkOutDate);
    Mono<BookingDTO> cancelBooking(String id);
    Mono<BookingDTO> confirmBooking(String id);
}
