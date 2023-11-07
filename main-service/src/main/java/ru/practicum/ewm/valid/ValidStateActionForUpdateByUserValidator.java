package ru.practicum.ewm.valid;

import ru.practicum.ewm.event.model.StateAction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidStateActionForUpdateByUserValidator implements ConstraintValidator<ValidStateActionForUpdateByUser, StateAction> {
    @Override
    public boolean isValid(StateAction value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.equals(StateAction.SEND_TO_REVIEW) || value.equals(StateAction.CANCEL_REVIEW);
        }
        return true;
    }
}