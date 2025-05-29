package com.galaxyviewtower.hotel.crud.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoomNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoomNumber {
    String message() default "Invalid room number format. Must be alphanumeric and start with a letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 