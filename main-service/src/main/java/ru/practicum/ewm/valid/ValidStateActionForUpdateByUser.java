package ru.practicum.ewm.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStateActionForUpdateByUserValidator.class)
public @interface ValidStateActionForUpdateByUser {
    String message() default "State action must be SEND_TO_REVIEW or CANCEL_REVIEW";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}