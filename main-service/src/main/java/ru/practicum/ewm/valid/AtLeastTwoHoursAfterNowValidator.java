package ru.practicum.ewm.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AtLeastTwoHoursAfterNowValidator implements ConstraintValidator<AtLeastTwoHoursAfterNow, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value != null) {
            return LocalDateTime.now().plusHours(2).isBefore(value);
        }
        return true;
    }
}