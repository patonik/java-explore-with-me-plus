package ru.practicum;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ApiError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        String message = e.getMessage();
        log.warn("ConstraintViolation: {}", message);
        List<String> errors = new ArrayList<>();
        errors.add(message);
        Throwable cause = e.getCause();
        while (cause != null) {
            errors.add(cause.getMessage());
            cause = cause.getCause();
        }
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringJoiner reason = new StringJoiner(", ");
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            reason.add(constraintViolation.getMessage());
        }
        return ApiError.builder()
            .errors(errors)
            .message(message)
            .reason(reason.toString())
            .httpStatus(HttpStatus.BAD_REQUEST)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
