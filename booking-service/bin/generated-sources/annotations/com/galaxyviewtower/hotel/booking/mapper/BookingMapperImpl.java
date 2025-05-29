package com.galaxyviewtower.hotel.booking.mapper;

import com.galaxyviewtower.hotel.booking.dto.BookingDTO;
import com.galaxyviewtower.hotel.booking.model.Booking;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T11:22:50-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public Booking toEntity(BookingDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Booking booking = new Booking();

        booking.setCheckInDate( dto.getCheckInDate() );
        booking.setCheckOutDate( dto.getCheckOutDate() );
        booking.setHotelId( dto.getHotelId() );
        booking.setNumberOfGuests( dto.getNumberOfGuests() );
        booking.setRoomId( dto.getRoomId() );
        booking.setRoomTypeId( dto.getRoomTypeId() );
        booking.setStatus( dto.getStatus() );
        booking.setTotalPrice( dto.getTotalPrice() );
        booking.setUserId( dto.getUserId() );

        return booking;
    }

    @Override
    public BookingDTO toDTO(Booking entity) {
        if ( entity == null ) {
            return null;
        }

        BookingDTO bookingDTO = new BookingDTO();

        bookingDTO.setBookedAt( entity.getBookedAt() );
        bookingDTO.setCheckInDate( entity.getCheckInDate() );
        bookingDTO.setCheckOutDate( entity.getCheckOutDate() );
        bookingDTO.setHotelId( entity.getHotelId() );
        bookingDTO.setId( entity.getId() );
        bookingDTO.setNumberOfGuests( entity.getNumberOfGuests() );
        bookingDTO.setRoomId( entity.getRoomId() );
        bookingDTO.setRoomTypeId( entity.getRoomTypeId() );
        bookingDTO.setStatus( entity.getStatus() );
        bookingDTO.setTotalPrice( entity.getTotalPrice() );
        bookingDTO.setUpdatedAt( entity.getUpdatedAt() );
        bookingDTO.setUserId( entity.getUserId() );

        return bookingDTO;
    }

    @Override
    public void updateEntityFromDTO(BookingDTO dto, Booking entity) {
        if ( dto == null ) {
            return;
        }

        entity.setCheckInDate( dto.getCheckInDate() );
        entity.setCheckOutDate( dto.getCheckOutDate() );
        entity.setHotelId( dto.getHotelId() );
        entity.setNumberOfGuests( dto.getNumberOfGuests() );
        entity.setRoomId( dto.getRoomId() );
        entity.setRoomTypeId( dto.getRoomTypeId() );
        entity.setStatus( dto.getStatus() );
        entity.setTotalPrice( dto.getTotalPrice() );
        entity.setUserId( dto.getUserId() );
    }
}
