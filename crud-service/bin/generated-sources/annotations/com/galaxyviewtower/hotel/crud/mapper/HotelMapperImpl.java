package com.galaxyviewtower.hotel.crud.mapper;

import com.galaxyviewtower.hotel.crud.model.gen.Hotel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T02:29:48-0400",
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

        hotel.setId( entityHotel.getId() != null ? entityHotel.getId() : null );
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

        hotel1.setId( hotel.getId() != null ? hotel.getId() : null );
        hotel1.setRating( hotel.getRating() != null ? java.math.BigDecimal.valueOf(hotel.getRating()) : null );

        return hotel1;
    }
}
