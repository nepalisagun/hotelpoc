package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.mapper.HotelMapper;
import com.galaxyviewtower.hotel.crud.model.Hotel;
import com.galaxyviewtower.hotel.crud.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

public class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private HotelMapper hotelMapper;
    @InjectMocks
    private HotelServiceImpl hotelService;

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
} 