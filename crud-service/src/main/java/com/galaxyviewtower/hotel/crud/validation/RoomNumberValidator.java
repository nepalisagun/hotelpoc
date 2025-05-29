package com.galaxyviewtower.hotel.crud.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoomNumberValidator implements ConstraintValidator<ValidRoomNumber, String> {
    
    @Override
    public boolean isValid(String roomNumber, ConstraintValidatorContext context) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return false;
        }
        
        // Room number must start with a letter and contain only letters, numbers, and hyphens
        return roomNumber.matches("^[A-Za-z][A-Za-z0-9-]*$");
    }
} 