package ru.practicum.mainservice.exception.model;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
