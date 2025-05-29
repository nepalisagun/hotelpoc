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
          "java(entityHotel.getId() != null ? entityHotel.getId() : null)") // Use String for id
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "address")
  @Mapping(
      target = "rating",
      expression =
          "java(entityHotel.getRating() != null ? entityHotel.getRating().floatValue() : null)")
  com.galaxyviewtower.hotel.crud.model.gen.Hotel toDto(
      com.galaxyviewtower.hotel.crud.model.Hotel entityHotel);

  // Map from OpenAPI model to entity
  @Mapping(
      target = "id",
      expression =
          "java(hotel.getId() != null ? hotel.getId() : null)") // Accept String id from OpenAPI
  // model
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "address")
  @Mapping(
      target = "rating",
      expression =
          "java(hotel.getRating() != null ? java.math.BigDecimal.valueOf(hotel.getRating()) : null)")
  com.galaxyviewtower.hotel.crud.model.Hotel toEntity(
      com.galaxyviewtower.hotel.crud.model.gen.Hotel hotel);
}
