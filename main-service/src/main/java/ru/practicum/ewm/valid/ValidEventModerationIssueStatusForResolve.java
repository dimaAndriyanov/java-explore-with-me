package ru.practicum.ewm.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEventModerationIssueStatusForResolveValidator.class)
public @interface ValidEventModerationIssueStatusForResolve {
    String message() default "Event moderation issue status must be OPENED or CLOSED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}