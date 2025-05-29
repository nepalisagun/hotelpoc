package com.galaxyviewtower.hotel.crud.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BedTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBedType {
    String message() default "Invalid bed type. Must be one of: SINGLE, DOUBLE, QUEEN, KING";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 