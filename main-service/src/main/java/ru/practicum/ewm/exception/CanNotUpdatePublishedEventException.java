package ru.practicum.ewm.exception;

public class CanNotUpdatePublishedEventException extends RuntimeException {
    public CanNotUpdatePublishedEventException(String message) {
        super(message);
    }
}