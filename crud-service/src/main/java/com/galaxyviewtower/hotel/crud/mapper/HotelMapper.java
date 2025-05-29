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
      expression = "java(entityHotel.getId() != null ? java.util.UUID.fromString(entityHotel.getId()) : null)")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "address")
  @Mapping(target = "city", source = "city")
  @Mapping(target = "country", source = "country")
  @Mapping(
      target = "rating",
      expression = "java(entityHotel.getRating() != null ? entityHotel.getRating().floatValue() : null)")
  @Mapping(target = "totalRooms", source = "totalRooms")
  @Mapping(target = "pricePerNight", source = "pricePerNight")
  @Mapping(target = "isActive", source = "active")
  com.galaxyviewtower.hotel.crud.model.gen.Hotel toDto(
      com.galaxyviewtower.hotel.crud.model.Hotel entityHotel);

  // Map from OpenAPI model to entity
  @Mapping(
      target = "id",
      expression = "java(hotel.getId() != null ? hotel.getId().toString() : null)")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "address")
  @Mapping(target = "city", source = "city")
  @Mapping(target = "country", source = "country")
  @Mapping(
      target = "rating",
      expression = "java(hotel.getRating() != null ? java.math.BigDecimal.valueOf(hotel.getRating()) : null)")
  @Mapping(target = "totalRooms", source = "totalRooms")
  @Mapping(target = "pricePerNight", source = "pricePerNight")
  @Mapping(target = "active", source = "isActive")
  com.galaxyviewtower.hotel.crud.model.Hotel toEntity(
      com.galaxyviewtower.hotel.crud.model.gen.Hotel hotel);
}
