package ru.practicum.mainservice.exception.model;

public class InvalidDateTimeException extends RuntimeException {
    public InvalidDateTimeException(String message) {
        super(message);
    }
}
