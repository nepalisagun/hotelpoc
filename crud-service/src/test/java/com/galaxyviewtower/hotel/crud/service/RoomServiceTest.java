package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.dto.RoomDTO;
import com.galaxyviewtower.hotel.crud.mapper.RoomMapper;
import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.repository.RoomRepository;
import com.galaxyviewtower.hotel.crud.service.impl.RoomServiceImpl;
import com.galaxyviewtower.hotel.crud.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {
        room = TestDataBuilder.createRoom();
        roomDTO = TestDataBuilder.createRoomDTO();
    }

    @Test
    void createRoom_Success() {
        when(roomMapper.toEntity(any(RoomDTO.class))).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(Mono.just(room));
        when(roomMapper.toDTO(any(Room.class))).thenReturn(roomDTO);

        StepVerifier.create(roomService.createRoom(roomDTO))
                .expectNext(roomDTO)
                .verifyComplete();

        verify(roomMapper).toEntity(roomDTO);
        verify(roomRepository).save(room);
        verify(roomMapper).toDTO(room);
    }

    @Test
    void getRoom_Success() {
        when(roomRepository.findById(anyString())).thenReturn(Mono.just(room));
        when(roomMapper.toDTO(any(Room.class))).thenReturn(roomDTO);

        StepVerifier.create(roomService.getRoom(room.getId()))
                .expectNext(roomDTO)
                .verifyComplete();

        verify(roomRepository).findById(room.getId());
        verify(roomMapper).toDTO(room);
    }

    @Test
    void getRoom_NotFound() {
        when(roomRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(roomService.getRoom(room.getId()))
                .expectError()
                .verify();

        verify(roomRepository).findById(room.getId());
        verify(roomMapper, never()).toDTO(any());
    }

    @Test
    void updateRoom_Success() {
        when(roomRepository.findById(anyString())).thenReturn(Mono.just(room));
        when(roomMapper.toEntity(any(RoomDTO.class))).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(Mono.just(room));
        when(roomMapper.toDTO(any(Room.class))).thenReturn(roomDTO);

        StepVerifier.create(roomService.updateRoom(room.getId(), roomDTO))
                .expectNext(roomDTO)
                .verifyComplete();

        verify(roomRepository).findById(room.getId());
        verify(roomMapper).toEntity(roomDTO);
        verify(roomRepository).save(room);
        verify(roomMapper).toDTO(room);
    }

    @Test
    void deleteRoom_Success() {
        when(roomRepository.deleteById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(roomService.deleteRoom(room.getId()))
                .verifyComplete();

        verify(roomRepository).deleteById(room.getId());
    }

    @Test
    void getRoomsByHotel_Success() {
        when(roomRepository.findByHotelId(anyString())).thenReturn(Flux.just(room));
        when(roomMapper.toDTO(any(Room.class))).thenReturn(roomDTO);

        StepVerifier.create(roomService.getRoomsByHotel(room.getHotelId()))
                .expectNext(roomDTO)
                .verifyComplete();

        verify(roomRepository).findByHotelId(room.getHotelId());
        verify(roomMapper).toDTO(room);
    }
} 