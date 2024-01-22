package ru.yakovlev.businesscalendar.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    /**
     * Secure Password requirements: <br>
     * <p>
     * Password must contain at least one digit [0-9]. <br>
     * Password must contain at least one lowercase Latin character [a-z]. <br>
     * Password must contain at least one uppercase Latin character [A-Z]. <br>
     * Password must contain at least one special character like ! @ # & ( ). <br>
     * Password must contain a length of at least 8 characters and a maximum of 20 characters.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$") &&
                !value.isEmpty();
    }

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
