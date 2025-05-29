package com.galaxyviewtower.hotel.booking.service.impl;

import com.galaxyviewtower.hotel.booking.client.CrudServiceClient;
import com.galaxyviewtower.hotel.booking.dto.AvailabilityRequest;
import com.galaxyviewtower.hotel.booking.dto.AvailabilityResponse;
import com.galaxyviewtower.hotel.booking.dto.RoomDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomTypeDTO;
import com.galaxyviewtower.hotel.booking.repository.BookingLedgerRepository;
import com.galaxyviewtower.hotel.booking.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final CrudServiceClient crudServiceClient;
    private final BookingLedgerRepository bookingLedgerRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    public Mono<AvailabilityResponse> checkAvailability(AvailabilityRequest request) {
        return crudServiceClient.getRoomTypeDetails(request.getHotelId(), request.getRoomTypeId())
                .flatMap(roomType -> bookingLedgerRepository.countOverlappingBookings(
                        request.getHotelId(),
                        request.getRoomTypeId(),
                        request.getCheckInDate(),
                        request.getCheckOutDate()
                ).map(bookedRooms -> {
                    int availableRooms = roomType.getTotalRooms() - bookedRooms.intValue();
                    boolean isAvailable = availableRooms >= request.getNumberOfRooms();
                    
                    return AvailabilityResponse.builder()
                            .available(isAvailable)
                            .hotelId(request.getHotelId())
                            .roomTypeId(request.getRoomTypeId())
                            .roomTypeName(roomType.getName())
                            .totalRooms(roomType.getTotalRooms())
                            .availableRooms(availableRooms)
                            .pricePerNight(roomType.getBasePricePerNight())
                            .message(isAvailable ? 
                                    String.format("%d rooms available", availableRooms) : 
                                    "Not enough rooms available")
                            .build();
                }));
    }

    @Override
    public Mono<AvailabilityResponse> getDetailedAvailability(String hotelId, String roomTypeId, 
                                                            String checkInDate, String checkOutDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate, DATE_FORMATTER);
        LocalDate checkOut = LocalDate.parse(checkOutDate, DATE_FORMATTER);

        return crudServiceClient.getRoomTypeDetails(hotelId, roomTypeId)
                .flatMap(roomType -> Mono.zip(
                        crudServiceClient.getAvailableRooms(hotelId, roomTypeId, checkIn, checkOut).collectList(),
                        bookingLedgerRepository.findOverlappingBookings(hotelId, roomTypeId, checkIn, checkOut).collectList()
                ).map(tuple -> {
                    List<RoomDTO> allRooms = tuple.getT1();
                    List<String> bookedRoomIds = tuple.getT2().stream()
                            .map(booking -> booking.getRoomId())
                            .toList();
                    
                    List<String> availableRoomNumbers = allRooms.stream()
                            .filter(room -> !bookedRoomIds.contains(room.getId()))
                            .map(RoomDTO::getRoomNumber)
                            .toList();

                    return AvailabilityResponse.builder()
                            .available(!availableRoomNumbers.isEmpty())
                            .hotelId(hotelId)
                            .roomTypeId(roomTypeId)
                            .roomTypeName(roomType.getName())
                            .totalRooms(roomType.getTotalRooms())
                            .availableRooms(availableRoomNumbers.size())
                            .pricePerNight(roomType.getBasePricePerNight())
                            .availableRoomNumbers(availableRoomNumbers)
                            .message(availableRoomNumbers.isEmpty() ? 
                                    "No rooms available for the selected dates" : 
                                    String.format("%d rooms available", availableRoomNumbers.size()))
                            .build();
                }));
    }
} 