package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.config.TestConfig;
import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    private Room room;

    @BeforeEach
    void setUp() {
        room = TestDataBuilder.createRoom();
        roomRepository.deleteAll().block();
    }

    @Test
    void save_Success() {
        Mono<Room> savedRoom = roomRepository.save(room);

        StepVerifier.create(savedRoom)
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getRoomNumber()).isEqualTo(room.getRoomNumber());
                    assertThat(saved.getHotelId()).isEqualTo(room.getHotelId());
                })
                .verifyComplete();
    }

    @Test
    void findById_Success() {
        Room savedRoom = roomRepository.save(room).block();

        StepVerifier.create(roomRepository.findById(savedRoom.getId()))
                .assertNext(found -> {
                    assertThat(found.getId()).isEqualTo(savedRoom.getId());
                    assertThat(found.getRoomNumber()).isEqualTo(savedRoom.getRoomNumber());
                })
                .verifyComplete();
    }

    @Test
    void findById_NotFound() {
        StepVerifier.create(roomRepository.findById("non-existent-id"))
                .verifyComplete();
    }

    @Test
    void findByHotelId_Success() {
        Room savedRoom = roomRepository.save(room).block();
        Room anotherRoom = TestDataBuilder.createRoom();
        anotherRoom.setHotelId(room.getHotelId());
        roomRepository.save(anotherRoom).block();

        StepVerifier.create(roomRepository.findByHotelId(room.getHotelId()).collectList())
                .assertNext(rooms -> {
                    assertThat(rooms).hasSize(2);
                    assertThat(rooms).allMatch(r -> r.getHotelId().equals(room.getHotelId()));
                })
                .verifyComplete();
    }

    @Test
    void deleteById_Success() {
        Room savedRoom = roomRepository.save(room).block();

        StepVerifier.create(roomRepository.deleteById(savedRoom.getId()))
                .verifyComplete();

        StepVerifier.create(roomRepository.findById(savedRoom.getId()))
                .verifyComplete();
    }

    @Test
    void findByRoomTypeId_Success() {
        Room savedRoom = roomRepository.save(room).block();
        Room anotherRoom = TestDataBuilder.createRoom();
        anotherRoom.setRoomTypeId(room.getRoomTypeId());
        roomRepository.save(anotherRoom).block();

        StepVerifier.create(roomRepository.findByRoomTypeId(room.getRoomTypeId()).collectList())
                .assertNext(rooms -> {
                    assertThat(rooms).hasSize(2);
                    assertThat(rooms).allMatch(r -> r.getRoomTypeId().equals(room.getRoomTypeId()));
                })
                .verifyComplete();
    }

    @Test
    void findByRoomNumber_Success() {
        Room savedRoom = roomRepository.save(room).block();

        StepVerifier.create(roomRepository.findByRoomNumber(savedRoom.getRoomNumber()))
                .assertNext(found -> {
                    assertThat(found.getId()).isEqualTo(savedRoom.getId());
                    assertThat(found.getRoomNumber()).isEqualTo(savedRoom.getRoomNumber());
                })
                .verifyComplete();
    }
} 