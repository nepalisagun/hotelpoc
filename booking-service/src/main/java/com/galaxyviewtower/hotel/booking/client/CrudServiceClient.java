package com.galaxyviewtower.hotel.booking.client;

import com.galaxyviewtower.hotel.booking.dto.HotelDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomTypeDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@HttpExchange("/api/v1")
public interface CrudServiceClient {
    @GetExchange("/hotels/{hotelId}/room-types/{roomTypeId}/availability")
    Mono<Boolean> checkRoomAvailability(
            @PathVariable String hotelId,
            @PathVariable String roomTypeId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam Integer numberOfRooms
    );

    @GetExchange("/hotels/{hotelId}/room-types/{roomTypeId}")
    Mono<RoomTypeDTO> getRoomTypeDetails(
            @PathVariable String hotelId,
            @PathVariable String roomTypeId
    );

    @GetExchange("/hotels/{hotelId}/room-types/{roomTypeId}/rooms")
    Flux<RoomDTO> getAvailableRooms(
            @PathVariable String hotelId,
            @PathVariable String roomTypeId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate
    );

    @GetExchange("/hotels/{hotelId}")
    Mono<HotelDTO> getHotelById(@PathVariable String hotelId);

    @GetExchange("/hotels/{hotelId}/availability")
    Mono<Boolean> checkHotelAvailability(@PathVariable String hotelId);

    Mono<HotelDTO> getHotel(String hotelId);
    Mono<RoomTypeDTO> getRoomType(String roomTypeId);
    Mono<Boolean> checkRoomAvailability(String hotelId, String roomTypeId, String checkInDate, String checkOutDate);
} 