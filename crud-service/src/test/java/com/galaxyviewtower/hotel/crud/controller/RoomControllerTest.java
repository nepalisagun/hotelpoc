package com.galaxyviewtower.hotel.crud.controller;

import com.galaxyviewtower.hotel.crud.config.TestConfig;
import com.galaxyviewtower.hotel.crud.dto.RoomDTO;
import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.repository.RoomRepository;
import com.galaxyviewtower.hotel.crud.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestConfig.class)
class RoomControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    private Room room;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {
        room = TestDataBuilder.createRoom();
        roomDTO = TestDataBuilder.createRoomDTO();
        
        // Clean up the database before each test
        roomRepository.deleteAll().block();
    }

    @Test
    void createRoom_Success() {
        webTestClient.post()
                .uri("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomDTO.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getRoomNumber()).isEqualTo(roomDTO.getRoomNumber());
                    assertThat(response.getFloor()).isEqualTo(roomDTO.getFloor());
                });
    }

    @Test
    void createRoom_InvalidData() {
        roomDTO.setRoomNumber("123"); // Invalid room number format

        webTestClient.post()
                .uri("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getRoom_Success() {
        Room savedRoom = roomRepository.save(room).block();

        webTestClient.get()
                .uri("/api/rooms/{id}", savedRoom.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoomDTO.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(savedRoom.getId());
                    assertThat(response.getRoomNumber()).isEqualTo(savedRoom.getRoomNumber());
                });
    }

    @Test
    void getRoom_NotFound() {
        webTestClient.get()
                .uri("/api/rooms/{id}", "non-existent-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateRoom_Success() {
        Room savedRoom = roomRepository.save(room).block();
        roomDTO.setRoomNumber("B202");

        webTestClient.put()
                .uri("/api/rooms/{id}", savedRoom.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoomDTO.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(savedRoom.getId());
                    assertThat(response.getRoomNumber()).isEqualTo("B202");
                });
    }

    @Test
    void deleteRoom_Success() {
        Room savedRoom = roomRepository.save(room).block();

        webTestClient.delete()
                .uri("/api/rooms/{id}", savedRoom.getId())
                .exchange()
                .expectStatus().isNoContent();

        // Verify room is deleted
        webTestClient.get()
                .uri("/api/rooms/{id}", savedRoom.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getRoomsByHotel_Success() {
        Room savedRoom = roomRepository.save(room).block();

        webTestClient.get()
                .uri("/api/rooms/hotel/{hotelId}", savedRoom.getHotelId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RoomDTO.class)
                .hasSize(1)
                .value(rooms -> {
                    assertThat(rooms.get(0).getId()).isEqualTo(savedRoom.getId());
                    assertThat(rooms.get(0).getHotelId()).isEqualTo(savedRoom.getHotelId());
                });
    }
} 