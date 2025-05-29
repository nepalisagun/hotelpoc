package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.model.Room;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface AvailabilityService {
    /**
     * Check if a specific room type is available for the given dates
     * @param hotelId The hotel ID
     * @param roomTypeId The room type ID
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @param numberOfRooms Number of rooms needed
     * @return true if available, false otherwise
     */
    Mono<Boolean> isRoomTypeAvailable(String hotelId, String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, int numberOfRooms);

    /**
     * Get all available rooms of a specific type for the given dates
     * @param hotelId The hotel ID
     * @param roomTypeId The room type ID
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return Flux of available rooms
     */
    Flux<Room> getAvailableRooms(String hotelId, String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Get the total number of available rooms of a specific type for the given dates
     * @param hotelId The hotel ID
     * @param roomTypeId The room type ID
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return The number of available rooms
     */
    Mono<Long> getAvailableRoomCount(String hotelId, String roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Mark rooms as unavailable for a specific period
     * @param roomIds List of room IDs to mark as unavailable
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return Mono<Void>
     */
    Mono<Void> markRoomsAsUnavailable(Flux<String> roomIds, LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Mark rooms as available after a booking period
     * @param roomIds List of room IDs to mark as available
     * @return Mono<Void>
     */
    Mono<Void> markRoomsAsAvailable(Flux<String> roomIds);
} 