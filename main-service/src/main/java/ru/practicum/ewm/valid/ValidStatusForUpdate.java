package ru.practicum.ewm.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStatusForUpdateValidator.class)
public @interface ValidStatusForUpdate {
    String message() default "Status must be CONFIRMED or REJECTED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}