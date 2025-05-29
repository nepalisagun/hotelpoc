package com.galaxyviewtower.hotel.booking.mapper;

import com.galaxyviewtower.hotel.booking.dto.BookingDTO;
import com.galaxyviewtower.hotel.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Booking toEntity(BookingDTO dto);
    
    BookingDTO toDTO(Booking entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(BookingDTO dto, @MappingTarget Booking entity);
} 