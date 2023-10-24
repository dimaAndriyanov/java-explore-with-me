package ru.practicum.ewm.exception;

public class EndBeforeStartException extends RuntimeException {
    public EndBeforeStartException(String message) {
        super(message);
    }
}