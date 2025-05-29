package com.galaxyviewtower.hotel.booking.integration;

import com.galaxyviewtower.hotel.booking.dto.BookingDTO;
import com.galaxyviewtower.hotel.booking.dto.HotelDTO;
import com.galaxyviewtower.hotel.booking.dto.RoomTypeDTO;
import com.galaxyviewtower.hotel.booking.model.Booking;
import com.galaxyviewtower.hotel.booking.repository.BookingRepository;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class BookingFlowIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private CrudServiceClient crudServiceClient;

    private HotelDTO testHotel;
    private RoomTypeDTO testRoomType;
    private BookingDTO testBookingDTO;

    @BeforeEach
    void setUp() {
        // Clean up the database
        bookingRepository.deleteAll().block();

        // Setup test hotel
        testHotel = new HotelDTO();
        testHotel.setId("1");
        testHotel.setName("Test Hotel");
        testHotel.setAddress("123 Test St");
        testHotel.setCity("Test City");
        testHotel.setCountry("Test Country");
        testHotel.setRating(new BigDecimal("4.5"));
        testHotel.setTotalRooms(100);
        testHotel.setPricePerNight(new BigDecimal("200.00"));
        testHotel.setIsActive(true);

        // Setup test room type
        testRoomType = new RoomTypeDTO();
        testRoomType.setId("1");
        testRoomType.setHotelId("1");
        testRoomType.setName("Deluxe Room");
        testRoomType.setDescription("Luxury room with city view");
        testRoomType.setCapacity(2);
        testRoomType.setPricePerNight(new BigDecimal("200.00"));
        testRoomType.setTotalRooms(10);
        testRoomType.setIsActive(true);

        // Setup test booking
        testBookingDTO = new BookingDTO();
        testBookingDTO.setHotelId("1");
        testBookingDTO.setRoomTypeId("1");
        testBookingDTO.setUserId("1");
        testBookingDTO.setCheckInDate(LocalDate.now().plusDays(1).toString());
        testBookingDTO.setCheckOutDate(LocalDate.now().plusDays(3).toString());
        testBookingDTO.setNumberOfGuests(2);
        testBookingDTO.setSpecialRequests("Test request");
    }

    @Test
    void testCompleteBookingFlow() {
        // Mock CRUD service responses
        when(crudServiceClient.getHotel("1")).thenReturn(Mono.just(testHotel));
        when(crudServiceClient.getRoomType("1")).thenReturn(Mono.just(testRoomType));
        when(crudServiceClient.checkRoomAvailability(any(), any(), any(), any()))
                .thenReturn(Mono.just(true));

        // Test booking creation
        StepVerifier.create(bookingService.createBooking(testBookingDTO))
                .expectNextMatches(booking -> {
                    assert booking.getStatus() == Booking.BookingStatus.PENDING;
                    assert booking.getTotalPrice() == 400.00; // 2 nights * 200.00
                    return true;
                })
                .verifyComplete();

        // Test booking retrieval
        StepVerifier.create(bookingService.getBookingsByUserId("1"))
                .expectNextCount(1)
                .verifyComplete();

        // Test booking confirmation
        StepVerifier.create(bookingService.getBookingsByUserId("1")
                .next()
                .flatMap(booking -> bookingService.confirmBooking(booking.getId())))
                .expectNextMatches(booking -> {
                    assert booking.getStatus() == Booking.BookingStatus.CONFIRMED;
                    return true;
                })
                .verifyComplete();

        // Test booking cancellation
        StepVerifier.create(bookingService.getBookingsByUserId("1")
                .next()
                .flatMap(booking -> bookingService.cancelBooking(booking.getId())))
                .expectNextMatches(booking -> {
                    assert booking.getStatus() == Booking.BookingStatus.CANCELLED;
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testBookingValidation() {
        // Test invalid check-in date (past date)
        testBookingDTO.setCheckInDate(LocalDate.now().minusDays(1).toString());
        
        StepVerifier.create(bookingService.createBooking(testBookingDTO))
                .expectErrorMatches(throwable -> 
                    throwable.getMessage().contains("Check-in date cannot be in the past"))
                .verify();

        // Test invalid check-out date (before check-in)
        testBookingDTO.setCheckInDate(LocalDate.now().plusDays(3).toString());
        testBookingDTO.setCheckOutDate(LocalDate.now().plusDays(2).toString());
        
        StepVerifier.create(bookingService.createBooking(testBookingDTO))
                .expectErrorMatches(throwable -> 
                    throwable.getMessage().contains("Check-out date must be after check-in date"))
                .verify();

        // Test invalid number of guests
        testBookingDTO.setCheckInDate(LocalDate.now().plusDays(1).toString());
        testBookingDTO.setCheckOutDate(LocalDate.now().plusDays(3).toString());
        testBookingDTO.setNumberOfGuests(0);
        
        StepVerifier.create(bookingService.createBooking(testBookingDTO))
                .expectErrorMatches(throwable -> 
                    throwable.getMessage().contains("Number of guests must be positive"))
                .verify();
    }

    @Test
    void testRoomAvailability() {
        // Mock room not available
        when(crudServiceClient.getHotel("1")).thenReturn(Mono.just(testHotel));
        when(crudServiceClient.getRoomType("1")).thenReturn(Mono.just(testRoomType));
        when(crudServiceClient.checkRoomAvailability(any(), any(), any(), any()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(bookingService.createBooking(testBookingDTO))
                .expectErrorMatches(throwable -> 
                    throwable.getMessage().contains("Room is not available for the selected dates"))
                .verify();
    }
} 