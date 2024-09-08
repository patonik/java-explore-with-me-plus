package ru.practicum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Future;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EventDateTimeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Future
public @interface LaterThan {
    String message() default "Invalid date/time value";

    /**
     * Interval in hours added to the minimum LocalDateTime
     * which is the LocalDateTime of callback by default
     */
    int value() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
