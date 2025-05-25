package com.galaxyviewtower.hotel.crud.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.Hotel;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class HotelServiceImplTest {

  @Mock private HotelRepository hotelRepository;
  @Mock private HotelMapper hotelMapper;
  @InjectMocks private HotelServiceImpl hotelService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllHotels() {
    Hotel hotel1 = new Hotel();
    hotel1.setId("1");
    Hotel hotel2 = new Hotel();
    hotel2.setId("2");
    when(hotelRepository.findAll()).thenReturn(Flux.just(hotel1, hotel2));

    StepVerifier.create(hotelService.getAllHotels())
        .expectNext(hotel1)
        .expectNext(hotel2)
        .verifyComplete();

    verify(hotelRepository, times(1)).findAll();
  }

  @Test
  void testGetAllHotels_EmptyList() {
    when(hotelRepository.findAll()).thenReturn(Flux.empty());

    StepVerifier.create(hotelService.getAllHotels()).expectComplete().verify();

    verify(hotelRepository, times(1)).findAll();
  }

  @Test
  void testGetHotelById_NotFound() {
    String hotelId = "999";
    when(hotelRepository.findById(hotelId)).thenReturn(Mono.empty());

    StepVerifier.create(hotelService.getHotelById(Integer.parseInt(hotelId)))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(hotelRepository, times(1)).findById(hotelId);
  }

  @Test
  void testCreateHotel_NullHotel() {
    StepVerifier.create(hotelService.createHotel(null))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(hotelRepository, never()).save(any());
  }

  @Test
  void testCreateHotel_InvalidId() {
    Hotel hotel = new Hotel();
    hotel.setId("invalid-id");

    StepVerifier.create(hotelService.createHotel(hotel))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(hotelRepository, never()).save(any());
  }

  @Test
  void testUpdateHotel_NotFound() {
    String hotelId = "999";
    Hotel hotel = new Hotel();
    hotel.setId(hotelId);
    when(hotelRepository.findById(hotelId)).thenReturn(Mono.empty());

    StepVerifier.create(hotelService.updateHotel(Integer.parseInt(hotelId), hotel))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(hotelRepository, times(1)).findById(hotelId);
    verify(hotelRepository, never()).save(any());
  }

  @Test
  void testDeleteHotel_NotFound() {
    String hotelId = "999";
    when(hotelRepository.findById(hotelId)).thenReturn(Mono.empty());

    StepVerifier.create(hotelService.deleteHotel(Integer.parseInt(hotelId)))
        .expectError(IllegalArgumentException.class)
        .verify();

    verify(hotelRepository, times(1)).findById(hotelId);
    verify(hotelRepository, never()).deleteById(anyString());
  }
}
