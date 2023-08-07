package ru.practicum.mainservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.mainservice.exception.model.ApiError;
import ru.practicum.mainservice.exception.model.ConflictException;
import ru.practicum.mainservice.exception.model.InvalidDateTimeException;
import ru.practicum.mainservice.exception.model.NotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("");
        return new ApiError(
                Arrays.asList(e.getStackTrace()),
                e.getMessage(),
                "Integrity constraint has been violated.",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidDateTimeException(final InvalidDateTimeException e) {
        log.warn("");
        return new ApiError(
                Arrays.asList(e.getStackTrace()),
                e.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("");
        return new ApiError(
                Arrays.asList(e.getStackTrace()),
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.warn("");
        return new ApiError(
                Arrays.asList(e.getStackTrace()),
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }
}
