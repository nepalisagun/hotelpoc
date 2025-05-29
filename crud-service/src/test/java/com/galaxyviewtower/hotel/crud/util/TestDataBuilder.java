package com.galaxyviewtower.hotel.crud.util;

import com.galaxyviewtower.hotel.crud.dto.RoomDTO;
import com.galaxyviewtower.hotel.crud.dto.RoomTypeDTO;
import com.galaxyviewtower.hotel.crud.model.Room;
import com.galaxyviewtower.hotel.crud.model.RoomType;

import java.util.UUID;

public class TestDataBuilder {
    
    public static RoomType createRoomType() {
        RoomType roomType = new RoomType();
        roomType.setId(UUID.randomUUID().toString());
        roomType.setHotelId(UUID.randomUUID().toString());
        roomType.setName("Deluxe Room");
        roomType.setDescription("Spacious room with ocean view");
        roomType.setBasePricePerNight(299.99);
        roomType.setTotalRooms(10);
        roomType.setMaxOccupancy(2);
        roomType.setBedType("KING");
        roomType.setRoomSize(400);
        roomType.setHasBalcony(true);
        roomType.setHasOceanView(true);
        roomType.setCancellationFee(50.0);
        roomType.setAmenities(new String[]{"WiFi", "Mini Bar", "Safe"});
        return roomType;
    }

    public static RoomTypeDTO createRoomTypeDTO() {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setHotelId(UUID.randomUUID().toString());
        dto.setName("Deluxe Room");
        dto.setDescription("Spacious room with ocean view");
        dto.setBasePricePerNight(299.99);
        dto.setTotalRooms(10);
        dto.setMaxOccupancy(2);
        dto.setBedType("KING");
        dto.setRoomSize(400);
        dto.setHasBalcony(true);
        dto.setHasOceanView(true);
        dto.setCancellationFee(50.0);
        dto.setAmenities(new String[]{"WiFi", "Mini Bar", "Safe"});
        return dto;
    }

    public static Room createRoom() {
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setHotelId(UUID.randomUUID().toString());
        room.setRoomTypeId(UUID.randomUUID().toString());
        room.setRoomNumber("A101");
        room.setFloor(1);
        room.setStatus("AVAILABLE");
        room.setNotes("Corner room with extra space");
        return room;
    }

    public static RoomDTO createRoomDTO() {
        RoomDTO dto = new RoomDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setHotelId(UUID.randomUUID().toString());
        dto.setRoomTypeId(UUID.randomUUID().toString());
        dto.setRoomNumber("A101");
        dto.setFloor(1);
        dto.setStatus("AVAILABLE");
        dto.setNotes("Corner room with extra space");
        return dto;
    }
} 