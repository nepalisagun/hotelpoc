package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.TestConfig;
import com.galaxyviewtower.hotel.crud.dto.HotelDTO;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class HotelServiceIntegrationTest {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        hotelRepository.deleteAll().block();
    }

    @Test
    void shouldCreateAndRetrieveHotel() {
        // Given
        HotelDTO hotelDTO = createSampleHotelDTO();

        // When
        Mono<HotelDTO> result = hotelService.createHotel(hotelDTO);

        // Then
        StepVerifier.create(result)
            .assertNext(savedHotel -> {
                assertThat(savedHotel.getId()).isNotNull();
                assertThat(savedHotel.getName()).isEqualTo(hotelDTO.getName());
                assertThat(savedHotel.getAddress()).isEqualTo(hotelDTO.getAddress());
                assertThat(savedHotel.getCity()).isEqualTo(hotelDTO.getCity());
                assertThat(savedHotel.getCountry()).isEqualTo(hotelDTO.getCountry());
                assertThat(savedHotel.getRating()).isEqualTo(hotelDTO.getRating());
                assertThat(savedHotel.getTotalRooms()).isEqualTo(hotelDTO.getTotalRooms());
                assertThat(savedHotel.getPricePerNight()).isEqualTo(hotelDTO.getPricePerNight());
            })
            .verifyComplete();
    }

    @Test
    void shouldUpdateHotel() {
        // Given
        HotelDTO hotelDTO = createSampleHotelDTO();
        HotelDTO savedHotel = hotelService.createHotel(hotelDTO).block();
        savedHotel.setName("Updated Hotel Name");
        savedHotel.setPricePerNight(new BigDecimal("299.99"));

        // When
        Mono<HotelDTO> result = hotelService.updateHotel(savedHotel.getId(), savedHotel);

        // Then
        StepVerifier.create(result)
            .assertNext(updatedHotel -> {
                assertThat(updatedHotel.getName()).isEqualTo("Updated Hotel Name");
                assertThat(updatedHotel.getPricePerNight()).isEqualTo(new BigDecimal("299.99"));
            })
            .verifyComplete();
    }

    @Test
    void shouldDeleteHotel() {
        // Given
        HotelDTO hotelDTO = createSampleHotelDTO();
        HotelDTO savedHotel = hotelService.createHotel(hotelDTO).block();

        // When
        Mono<Void> result = hotelService.deleteHotel(savedHotel.getId());

        // Then
        StepVerifier.create(result)
            .verifyComplete();

        StepVerifier.create(hotelService.getHotelById(savedHotel.getId()))
            .expectError()
            .verify();
    }

    @Test
    void shouldSearchHotelsByCityAndCountry() {
        // Given
        HotelDTO hotel1 = createSampleHotelDTO();
        hotel1.setCity("New York");
        hotel1.setCountry("USA");

        HotelDTO hotel2 = createSampleHotelDTO();
        hotel2.setCity("London");
        hotel2.setCountry("UK");

        hotelService.createHotel(hotel1).block();
        hotelService.createHotel(hotel2).block();

        // When
        Mono<List<HotelDTO>> result = hotelService.searchHotels("New York", "USA", null, null);

        // Then
        StepVerifier.create(result)
            .assertNext(hotels -> {
                assertThat(hotels).hasSize(1);
                assertThat(hotels.get(0).getCity()).isEqualTo("New York");
                assertThat(hotels.get(0).getCountry()).isEqualTo("USA");
            })
            .verifyComplete();
    }

    private HotelDTO createSampleHotelDTO() {
        return HotelDTO.builder()
            .name("Test Hotel")
            .address("123 Test Street")
            .city("Test City")
            .country("Test Country")
            .rating(new BigDecimal("4.5"))
            .totalRooms(100)
            .pricePerNight(new BigDecimal("199.99"))
            .phoneNumber("+1-555-0123")
            .email("test@hotel.com")
            .description("Test hotel description")
            .amenities(Arrays.asList("Pool", "Spa", "Restaurant"))
            .checkInTime("14:00")
            .checkOutTime("12:00")
            .build();
    }
} 