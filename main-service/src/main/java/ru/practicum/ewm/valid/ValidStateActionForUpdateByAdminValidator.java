package ru.practicum.ewm.valid;

import ru.practicum.ewm.event.model.StateAction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidStateActionForUpdateByAdminValidator implements ConstraintValidator<ValidStateActionForUpdateByAdmin, StateAction> {
    @Override
    public boolean isValid(StateAction value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.equals(StateAction.PUBLISH_EVENT) || value.equals(StateAction.REJECT_EVENT);
        }
        return true;
    }
}