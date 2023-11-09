package ru.practicum.ewm.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCompilationForUpdateValidator.class)
public @interface ValidCompilationForUpdate {
    String message() default "At least on field of Compilation request must not be null or empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}