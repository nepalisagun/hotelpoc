package com.galaxyviewtower.hotel.crud.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HotelMapper {
  HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);

  // Map from entity to OpenAPI model
  @Mapping(
      target = "id",
      expression =
          "java(entityHotel.getId() != null ? Integer.valueOf(entityHotel.getId()) : null)")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "city")
  @Mapping(
      target = "rating",
      expression =
          "java(entityHotel.getPricePerNight() != null ? entityHotel.getPricePerNight().floatValue() : null)")
  com.galaxyviewtower.hotel.crud.model.gen.Hotel toDto(
      com.galaxyviewtower.hotel.crud.model.Hotel entityHotel);

  // Map from OpenAPI model to entity
  @Mapping(
      target = "id",
      expression = "java(hotel.getId() != null ? hotel.getId().toString() : null)")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "city", source = "address")
  @Mapping(
      target = "pricePerNight",
      expression =
          "java(hotel.getRating() != null ? java.math.BigDecimal.valueOf(hotel.getRating()) : null)")
  com.galaxyviewtower.hotel.crud.model.Hotel toEntity(
      com.galaxyviewtower.hotel.crud.model.gen.Hotel hotel);
}
