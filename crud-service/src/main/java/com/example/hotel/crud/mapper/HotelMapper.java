package com.example.hotel.crud.mapper;

import com.example.hotel.crud.model.Hotel;
import com.example.hotel.crud.model.gen.HotelGen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HotelMapper {
  HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);

  @Mapping(
      target = "id",
      expression = "java(hotel.getId() != null ? Integer.valueOf(hotel.getId()) : null)")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "address", source = "city")
  @Mapping(
      target = "rating",
      expression =
          "java(hotel.getPricePerNight() != null ? hotel.getPricePerNight().floatValue() : null)")
  HotelGen toDto(Hotel hotel);

  @Mapping(
      target = "id",
      expression =
          "java(dto.getId() != null ? dto.getId().toString() : java.util.UUID.randomUUID().toString())")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "city", source = "address")
  @Mapping(
      target = "pricePerNight",
      expression =
          "java(dto.getRating() != null ? java.math.BigDecimal.valueOf(dto.getRating()) : null)")
  Hotel toEntity(HotelGen dto);
}
