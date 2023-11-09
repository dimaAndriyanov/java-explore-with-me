package ru.practicum.ewm.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastTwoHoursAfterNowValidator.class)
public @interface AtLeastTwoHoursAfterNow {
    String message() default "Event date must be at least 2 hours from now";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}