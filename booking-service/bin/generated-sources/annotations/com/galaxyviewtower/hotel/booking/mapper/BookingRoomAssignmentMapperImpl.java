package com.galaxyviewtower.hotel.booking.mapper;

import com.galaxyviewtower.hotel.booking.dto.BookingRoomAssignmentDTO;
import com.galaxyviewtower.hotel.booking.model.BookingRoomAssignment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T11:22:50-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class BookingRoomAssignmentMapperImpl implements BookingRoomAssignmentMapper {

    @Override
    public BookingRoomAssignment toEntity(BookingRoomAssignmentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        BookingRoomAssignment bookingRoomAssignment = new BookingRoomAssignment();

        bookingRoomAssignment.setBookingId( dto.getBookingId() );
        bookingRoomAssignment.setRoomId( dto.getRoomId() );

        return bookingRoomAssignment;
    }

    @Override
    public BookingRoomAssignmentDTO toDTO(BookingRoomAssignment entity) {
        if ( entity == null ) {
            return null;
        }

        BookingRoomAssignmentDTO bookingRoomAssignmentDTO = new BookingRoomAssignmentDTO();

        bookingRoomAssignmentDTO.setAssignedDate( entity.getAssignedDate() );
        bookingRoomAssignmentDTO.setBookingId( entity.getBookingId() );
        bookingRoomAssignmentDTO.setId( entity.getId() );
        bookingRoomAssignmentDTO.setRoomId( entity.getRoomId() );

        return bookingRoomAssignmentDTO;
    }

    @Override
    public void updateEntityFromDTO(BookingRoomAssignmentDTO dto, BookingRoomAssignment entity) {
        if ( dto == null ) {
            return;
        }

        entity.setBookingId( dto.getBookingId() );
        entity.setRoomId( dto.getRoomId() );
    }
}
