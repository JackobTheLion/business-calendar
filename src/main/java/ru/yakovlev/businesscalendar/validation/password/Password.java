package ru.yakovlev.businesscalendar.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default """
            Secure Password requirements

            Password must contain at least one digit [0-9].
            Password must contain at least one lowercase Latin character [a-z].
            Password must contain at least one uppercase Latin character [A-Z].
            Password must contain at least one special character like ! @ # & ( ).
            Password must contain a length of at least 8 characters and a maximum of 20 characters.""";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
