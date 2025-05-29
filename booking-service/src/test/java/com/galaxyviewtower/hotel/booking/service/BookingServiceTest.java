package com.galaxyviewtower.hotel.booking.service;

import com.galaxyviewtower.hotel.booking.client.CrudServiceClient;
import com.galaxyviewtower.hotel.booking.dto.HotelDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomTypeDTO;
import com.galaxyviewtower.hotel.booking.dto.request.BookingRequestDto;
import com.galaxyviewtower.hotel.booking.dto.response.BookingResponseDto;
import com.galaxyviewtower.hotel.booking.exception.BookingException;
import com.galaxyviewtower.hotel.booking.model.Booking;
import com.galaxyviewtower.hotel.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CrudServiceClient crudServiceClient;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequestDto testBookingRequest;
    private HotelDTO testHotel;
    private RoomTypeDTO testRoomType;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Setup test booking request
        testBookingRequest = new BookingRequestDto();
        testBookingRequest.setHotelId("test-hotel-id");
        testBookingRequest.setRoomTypeId("test-room-type-id");
        testBookingRequest.setUserId("test-user-id");
        testBookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        testBookingRequest.setCheckOutDate(LocalDate.now().plusDays(2));
        testBookingRequest.setNumberOfGuests(2);

        // Setup test hotel
        testHotel = new HotelDTO();
        testHotel.setId("test-hotel-id");
        testHotel.setName("Test Hotel");
        testHotel.setPricePerNight(new BigDecimal("299.99"));
        testHotel.setTotalRooms(100);

        // Setup test room type
        testRoomType = new RoomTypeDTO();
        testRoomType.setId("test-room-type-id");
        testRoomType.setHotelId("test-hotel-id");
        testRoomType.setName("Deluxe Room");
        testRoomType.setPricePerNight(new BigDecimal("299.99"));
        testRoomType.setTotalRooms(50);

        // Setup test booking
        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID().toString());
        testBooking.setHotelId(testBookingRequest.getHotelId());
        testBooking.setRoomTypeId(testBookingRequest.getRoomTypeId());
        testBooking.setUserId(testBookingRequest.getUserId());
        testBooking.setCheckInDate(testBookingRequest.getCheckInDate());
        testBooking.setCheckOutDate(testBookingRequest.getCheckOutDate());
        testBooking.setNumberOfGuests(testBookingRequest.getNumberOfGuests());
        testBooking.setTotalPrice(new BigDecimal("299.99"));
        testBooking.setStatus(Booking.BookingStatus.PENDING);
        testBooking.setBookedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBooking_Success() {
        when(crudServiceClient.getHotel(testBookingRequest.getHotelId()))
                .thenReturn(Mono.just(testHotel));
        when(crudServiceClient.getRoomType(testBookingRequest.getRoomTypeId()))
                .thenReturn(Mono.just(testRoomType));
        when(crudServiceClient.checkRoomAvailability(
                testBookingRequest.getHotelId(),
                testBookingRequest.getRoomTypeId(),
                testBookingRequest.getCheckInDate(),
                testBookingRequest.getCheckOutDate(),
                testBookingRequest.getNumberOfGuests()
        )).thenReturn(Mono.just(true));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(Mono.just(testBooking));

        StepVerifier.create(bookingService.createBooking(testBookingRequest))
                .expectNextMatches(response -> 
                    response.getId().equals(testBooking.getId()) &&
                    response.getHotelId().equals(testBooking.getHotelId()) &&
                    response.getStatus().equals(BookingResponseDto.BookingStatus.PENDING)
                )
                .verifyComplete();
    }

    @Test
    void testCreateBooking_HotelNotFound() {
        when(crudServiceClient.getHotel(testBookingRequest.getHotelId()))
                .thenReturn(Mono.error(new BookingException("Hotel not found")));

        StepVerifier.create(bookingService.createBooking(testBookingRequest))
                .expectError(BookingException.class)
                .verify();
    }

    @Test
    void testCreateBooking_RoomNotAvailable() {
        when(crudServiceClient.getHotel(testBookingRequest.getHotelId()))
                .thenReturn(Mono.just(testHotel));
        when(crudServiceClient.getRoomType(testBookingRequest.getRoomTypeId()))
                .thenReturn(Mono.just(testRoomType));
        when(crudServiceClient.checkRoomAvailability(
                testBookingRequest.getHotelId(),
                testBookingRequest.getRoomTypeId(),
                testBookingRequest.getCheckInDate(),
                testBookingRequest.getCheckOutDate(),
                testBookingRequest.getNumberOfGuests()
        )).thenReturn(Mono.just(false));

        StepVerifier.create(bookingService.createBooking(testBookingRequest))
                .expectError(BookingException.class)
                .verify();
    }

    @Test
    void testCancelBooking_Success() {
        testBooking.setStatus(Booking.BookingStatus.PENDING);
        when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Mono.just(testBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(Mono.just(testBooking));

        StepVerifier.create(bookingService.cancelBooking(testBooking.getId()))
                .expectNextMatches(response -> 
                    response.getId().equals(testBooking.getId()) &&
                    response.getStatus().equals(BookingResponseDto.BookingStatus.CANCELLED)
                )
                .verifyComplete();
    }

    @Test
    void testCancelBooking_AlreadyCancelled() {
        testBooking.setStatus(Booking.BookingStatus.CANCELLED);
        when(bookingRepository.findById(testBooking.getId()))
                .thenReturn(Mono.just(testBooking));

        StepVerifier.create(bookingService.cancelBooking(testBooking.getId()))
                .expectError(BookingException.class)
                .verify();
    }

    @Test
    void testCheckRoomAvailability_Success() {
        when(crudServiceClient.checkRoomAvailability(
                testBookingRequest.getHotelId(),
                testBookingRequest.getRoomTypeId(),
                testBookingRequest.getCheckInDate(),
                testBookingRequest.getCheckOutDate(),
                testBookingRequest.getNumberOfGuests()
        )).thenReturn(Mono.just(true));

        StepVerifier.create(bookingService.checkRoomAvailability(
                testBookingRequest.getHotelId(),
                testBookingRequest.getRoomTypeId(),
                testBookingRequest.getCheckInDate(),
                testBookingRequest.getCheckOutDate(),
                testBookingRequest.getNumberOfGuests()
        ))
                .expectNext(true)
                .verifyComplete();
    }
} 