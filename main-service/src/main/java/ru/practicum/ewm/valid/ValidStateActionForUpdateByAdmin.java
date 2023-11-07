package ru.practicum.ewm.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStateActionForUpdateByAdminValidator.class)
public @interface ValidStateActionForUpdateByAdmin {
    String message() default "State action must be PUBLISH_EVENT or REJECT_EVENT";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}