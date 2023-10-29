package ru.practicum.ewm.exception;

public class TimestampInFutureException extends RuntimeException {
    public TimestampInFutureException(String message) {
        super(message);
    }
}