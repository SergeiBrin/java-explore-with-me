package ru.practicum.statservice.exception.model;

public class InvalidDateTimeException extends RuntimeException {
    public InvalidDateTimeException(String message) {
        super(message);
    }
}
