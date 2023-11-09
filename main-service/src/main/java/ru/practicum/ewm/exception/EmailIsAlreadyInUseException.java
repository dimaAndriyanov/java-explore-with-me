package ru.practicum.ewm.exception;

public class EmailIsAlreadyInUseException extends RuntimeException {
    public EmailIsAlreadyInUseException(String message) {
        super(message);
    }
}