package com.galaxyviewtower.hotel.crud.repository;

import com.galaxyviewtower.hotel.crud.model.Hotel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface HotelRepository extends R2dbcRepository<Hotel, String> {}
