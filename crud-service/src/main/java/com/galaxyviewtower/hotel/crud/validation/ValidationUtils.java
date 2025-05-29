package com.galaxyviewtower.hotel.crud.validation;

import org.springframework.util.StringUtils;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-']{2,100}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-',.#]{5,255}$");
    private static final Pattern CITY_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-']{2,100}$");
    private static final Pattern COUNTRY_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-']{2,100}$");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\p{P}\\p{Z}\\s]{0,1000}$");
    private static final Pattern AMENITIES_PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\p{P}\\p{Z}\\s]{0,500}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidAddress(String address) {
        return address != null && ADDRESS_PATTERN.matcher(address).matches();
    }

    public static boolean isValidCity(String city) {
        return city != null && CITY_PATTERN.matcher(city).matches();
    }

    public static boolean isValidCountry(String country) {
        return country != null && COUNTRY_PATTERN.matcher(country).matches();
    }

    public static boolean isValidDescription(String description) {
        return description == null || DESCRIPTION_PATTERN.matcher(description).matches();
    }

    public static boolean isValidAmenities(String amenities) {
        return amenities == null || AMENITIES_PATTERN.matcher(amenities).matches();
    }

    public static String sanitizeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        // Remove any potential XSS patterns
        return input.replaceAll("<[^>]*>", "")
                   .replaceAll("javascript:", "")
                   .replaceAll("on\\w+", "")
                   .trim();
    }

    public static boolean isValidRating(Double rating) {
        return rating != null && rating >= 0.0 && rating <= 5.0;
    }

    public static boolean isValidTotalRooms(Integer totalRooms) {
        return totalRooms != null && totalRooms > 0 && totalRooms <= 10000;
    }

    public static boolean isValidPrice(Double price) {
        return price != null && price >= 0.0 && price <= 100000.0;
    }
} 