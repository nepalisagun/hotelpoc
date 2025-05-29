package com.galaxyviewtower.hotel.booking.exception;

public class HotelServiceException extends RuntimeException {
    private final String hotelId;
    private final String errorType;

    public HotelServiceException(String message, String hotelId, String errorType) {
        super(message);
        this.hotelId = hotelId;
        this.errorType = errorType;
    }

    public HotelServiceException(String message, String hotelId, String errorType, Throwable cause) {
        super(message, cause);
        this.hotelId = hotelId;
        this.errorType = errorType;
    }

    public String getHotelId() {
        return hotelId;
    }

    public String getErrorType() {
        return errorType;
    }
} 