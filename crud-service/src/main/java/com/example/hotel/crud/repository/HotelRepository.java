package com.example.hotel.crud.repository;

import com.example.hotel.crud.model.Hotel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface HotelRepository extends R2dbcRepository<Hotel, String> {}
