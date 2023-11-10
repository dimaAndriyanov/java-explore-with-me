package ru.practicum.ewm.valid;

import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssueStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidEventModerationIssueStatusForResolveValidator implements
        ConstraintValidator<ValidEventModerationIssueStatusForResolve, EventModerationIssueStatus> {
    @Override
    public boolean isValid(EventModerationIssueStatus value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.equals(EventModerationIssueStatus.OPENED) || value.equals(EventModerationIssueStatus.CLOSED);
        }
        return true;
    }
}