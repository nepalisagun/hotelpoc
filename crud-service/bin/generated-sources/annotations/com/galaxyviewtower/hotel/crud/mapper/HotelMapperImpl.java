package com.galaxyviewtower.hotel.crud.mapper;

import com.galaxyviewtower.hotel.crud.model.gen.Hotel;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T11:42:39-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class HotelMapperImpl implements HotelMapper {

    @Override
    public Hotel toDto(com.galaxyviewtower.hotel.crud.model.Hotel entityHotel) {
        if ( entityHotel == null ) {
            return null;
        }

        Hotel hotel = new Hotel();

        hotel.setName( entityHotel.getName() );
        hotel.setAddress( entityHotel.getAddress() );
        hotel.setCity( entityHotel.getCity() );
        hotel.setCountry( entityHotel.getCountry() );
        hotel.setTotalRooms( entityHotel.getTotalRooms() );
        if ( entityHotel.getPricePerNight() != null ) {
            hotel.setPricePerNight( entityHotel.getPricePerNight().floatValue() );
        }
        hotel.setIsActive( entityHotel.isActive() );
        hotel.setPhoneNumber( entityHotel.getPhoneNumber() );
        hotel.setEmail( entityHotel.getEmail() );
        hotel.setDescription( entityHotel.getDescription() );
        hotel.setAmenities( entityHotel.getAmenities() );
        hotel.setCheckInTime( entityHotel.getCheckInTime() );
        hotel.setCheckOutTime( entityHotel.getCheckOutTime() );

        hotel.setId( entityHotel.getId() != null ? java.util.UUID.fromString(entityHotel.getId()) : null );
        hotel.setRating( entityHotel.getRating() != null ? entityHotel.getRating().floatValue() : null );

        return hotel;
    }

    @Override
    public com.galaxyviewtower.hotel.crud.model.Hotel toEntity(Hotel hotel) {
        if ( hotel == null ) {
            return null;
        }

        com.galaxyviewtower.hotel.crud.model.Hotel hotel1 = new com.galaxyviewtower.hotel.crud.model.Hotel();

        hotel1.setName( hotel.getName() );
        hotel1.setAddress( hotel.getAddress() );
        hotel1.setCity( hotel.getCity() );
        hotel1.setCountry( hotel.getCountry() );
        if ( hotel.getTotalRooms() != null ) {
            hotel1.setTotalRooms( hotel.getTotalRooms() );
        }
        if ( hotel.getPricePerNight() != null ) {
            hotel1.setPricePerNight( BigDecimal.valueOf( hotel.getPricePerNight() ) );
        }
        if ( hotel.getIsActive() != null ) {
            hotel1.setActive( hotel.getIsActive() );
        }
        hotel1.setAmenities( hotel.getAmenities() );
        hotel1.setCheckInTime( hotel.getCheckInTime() );
        hotel1.setCheckOutTime( hotel.getCheckOutTime() );
        hotel1.setDescription( hotel.getDescription() );
        hotel1.setEmail( hotel.getEmail() );
        hotel1.setPhoneNumber( hotel.getPhoneNumber() );

        hotel1.setId( hotel.getId() != null ? hotel.getId().toString() : null );
        hotel1.setRating( hotel.getRating() != null ? java.math.BigDecimal.valueOf(hotel.getRating()) : null );

        return hotel1;
    }
}
