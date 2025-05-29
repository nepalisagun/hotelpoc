package com.galaxyviewtower.hotel.crud.exception;

public class InvalidHotelDataException extends RuntimeException {
    public InvalidHotelDataException(String message) {
        super(message);
    }

    public InvalidHotelDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 