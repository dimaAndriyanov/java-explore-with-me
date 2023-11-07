package ru.practicum.ewm.valid;

import ru.practicum.ewm.event.participation.request.model.Status;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidStatusForUpdateValidator implements ConstraintValidator<ValidStatusForUpdate, Status> {
    @Override
    public boolean isValid(Status value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.equals(Status.CONFIRMED) || value.equals(Status.REJECTED);
        }
        return true;
    }
}