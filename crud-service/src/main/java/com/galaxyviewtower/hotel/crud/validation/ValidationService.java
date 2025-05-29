package com.galaxyviewtower.hotel.crud.validation;

import com.galaxyviewtower.hotel.crud.dto.HotelDTO;
import com.galaxyviewtower.hotel.crud.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    public void validateHotel(HotelDTO hotel) {
        List<String> errors = new ArrayList<>();

        if (!ValidationUtils.isValidName(hotel.getName())) {
            errors.add("Invalid hotel name");
        }

        if (!ValidationUtils.isValidAddress(hotel.getAddress())) {
            errors.add("Invalid address");
        }

        if (!ValidationUtils.isValidCity(hotel.getCity())) {
            errors.add("Invalid city");
        }

        if (!ValidationUtils.isValidCountry(hotel.getCountry())) {
            errors.add("Invalid country");
        }

        if (!ValidationUtils.isValidRating(hotel.getRating())) {
            errors.add("Rating must be between 0.0 and 5.0");
        }

        if (!ValidationUtils.isValidTotalRooms(hotel.getTotalRooms())) {
            errors.add("Total rooms must be between 1 and 10000");
        }

        if (!ValidationUtils.isValidPrice(hotel.getPricePerNight())) {
            errors.add("Price per night must be between 0.0 and 100000.0");
        }

        if (hotel.getPhoneNumber() != null && !ValidationUtils.isValidPhone(hotel.getPhoneNumber())) {
            errors.add("Invalid phone number");
        }

        if (hotel.getEmail() != null && !ValidationUtils.isValidEmail(hotel.getEmail())) {
            errors.add("Invalid email address");
        }

        if (!ValidationUtils.isValidDescription(hotel.getDescription())) {
            errors.add("Invalid description");
        }

        if (!ValidationUtils.isValidAmenities(hotel.getAmenities())) {
            errors.add("Invalid amenities");
        }

        if (!ValidationUtils.isValidTime(hotel.getCheckInTime())) {
            errors.add("Invalid check-in time format (HH:mm)");
        }

        if (!ValidationUtils.isValidTime(hotel.getCheckOutTime())) {
            errors.add("Invalid check-out time format (HH:mm)");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public String sanitizeInput(String input) {
        return ValidationUtils.sanitizeInput(input);
    }
} 