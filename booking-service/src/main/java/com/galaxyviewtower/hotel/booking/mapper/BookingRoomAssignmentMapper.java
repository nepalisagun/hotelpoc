package com.galaxyviewtower.hotel.booking.mapper;

import com.galaxyviewtower.hotel.booking.dto.BookingRoomAssignmentDTO;
import com.galaxyviewtower.hotel.booking.model.BookingRoomAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingRoomAssignmentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedDate", ignore = true)
    BookingRoomAssignment toEntity(BookingRoomAssignmentDTO dto);
    
    BookingRoomAssignmentDTO toDTO(BookingRoomAssignment entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedDate", ignore = true)
    void updateEntityFromDTO(BookingRoomAssignmentDTO dto, @MappingTarget BookingRoomAssignment entity);
} 