package com.galaxyviewtower.hotel.crud.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BedTypeValidator implements ConstraintValidator<ValidBedType, String> {
    private static final Set<String> VALID_BED_TYPES = new HashSet<>(
        Arrays.asList("SINGLE", "DOUBLE", "QUEEN", "KING")
    );
    
    @Override
    public boolean isValid(String bedType, ConstraintValidatorContext context) {
        if (bedType == null || bedType.trim().isEmpty()) {
            return false;
        }
        
        return VALID_BED_TYPES.contains(bedType.toUpperCase());
    }
} 