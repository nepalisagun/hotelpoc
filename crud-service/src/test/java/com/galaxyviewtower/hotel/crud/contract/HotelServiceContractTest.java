package com.galaxyviewtower.hotel.crud.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.galaxyviewtower.hotel.crud.dto.HotelDTO;
import com.galaxyviewtower.hotel.crud.service.HotelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "hotel-service", port = "8080")
@ActiveProfiles("test")
class HotelServiceContractTest {

    @Autowired
    private HotelService hotelService;

    @Pact(consumer = "booking-service")
    public RequestResponsePact createHotelPact(PactDslWithProvider builder) {
        return builder
            .given("a new hotel")
            .uponReceiving("a request to create a hotel")
            .path("/api/v1/hotels")
            .method("POST")
            .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(createSampleHotelDTO())
            .willRespondWith()
            .status(201)
            .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(createSampleHotelResponse())
            .toPact();
    }

    @Pact(consumer = "booking-service")
    public RequestResponsePact getHotelPact(PactDslWithProvider builder) {
        return builder
            .given("a hotel exists")
            .uponReceiving("a request to get a hotel")
            .path("/api/v1/hotels/1")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(createSampleHotelResponse())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createHotelPact")
    void shouldCreateHotel(MockServer mockServer) {
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
            })
            .verifyComplete();
    }

    @Test
    @PactTestFor(pactMethod = "getHotelPact")
    void shouldGetHotel(MockServer mockServer) {
        // When
        Mono<HotelDTO> result = hotelService.getHotelById("1");

        // Then
        StepVerifier.create(result)
            .assertNext(hotel -> {
                assertThat(hotel.getId()).isEqualTo("1");
                assertThat(hotel.getName()).isEqualTo("Test Hotel");
                assertThat(hotel.getAddress()).isEqualTo("123 Test Street");
            })
            .verifyComplete();
    }

    private String createSampleHotelDTO() {
        return """
            {
                "name": "Test Hotel",
                "address": "123 Test Street",
                "city": "Test City",
                "country": "Test Country",
                "rating": 4.5,
                "totalRooms": 100,
                "pricePerNight": 199.99,
                "phoneNumber": "+1-555-0123",
                "email": "test@hotel.com",
                "description": "Test hotel description",
                "amenities": ["Pool", "Spa", "Restaurant"],
                "checkInTime": "14:00",
                "checkOutTime": "12:00"
            }
            """;
    }

    private String createSampleHotelResponse() {
        return """
            {
                "id": "1",
                "name": "Test Hotel",
                "address": "123 Test Street",
                "city": "Test City",
                "country": "Test Country",
                "rating": 4.5,
                "totalRooms": 100,
                "pricePerNight": 199.99,
                "phoneNumber": "+1-555-0123",
                "email": "test@hotel.com",
                "description": "Test hotel description",
                "amenities": ["Pool", "Spa", "Restaurant"],
                "checkInTime": "14:00",
                "checkOutTime": "12:00",
                "isActive": true,
                "createdAt": "2024-03-14T12:00:00Z",
                "updatedAt": "2024-03-14T12:00:00Z"
            }
            """;
    }
} 