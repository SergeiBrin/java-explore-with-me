package ru.practicum.mainservice.event.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureDateTimeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDateTime {
    String message() default "Date and time cannot be earlier than two hours from the current moment";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

