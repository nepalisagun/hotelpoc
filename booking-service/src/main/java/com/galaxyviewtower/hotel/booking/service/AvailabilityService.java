package com.galaxyviewtower.hotel.booking.service;

import com.galaxyviewtower.hotel.booking.dto.AvailabilityRequest;
import com.galaxyviewtower.hotel.booking.dto.AvailabilityResponse;
import reactor.core.publisher.Mono;

public interface AvailabilityService {
    /**
     * Check room availability for a specific hotel and room type
     * @param request The availability request containing hotel, room type, dates, and number of rooms
     * @return AvailabilityResponse containing availability details
     */
    Mono<AvailabilityResponse> checkAvailability(AvailabilityRequest request);

    /**
     * Get detailed availability information for a specific hotel and room type
     * @param hotelId The hotel ID
     * @param roomTypeId The room type ID
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return AvailabilityResponse with detailed room information
     */
    Mono<AvailabilityResponse> getDetailedAvailability(String hotelId, String roomTypeId, 
                                                      String checkInDate, String checkOutDate);
} 