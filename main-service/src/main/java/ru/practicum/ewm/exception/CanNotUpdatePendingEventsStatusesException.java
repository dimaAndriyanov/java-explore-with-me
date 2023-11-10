package ru.practicum.ewm.exception;

public class CanNotUpdatePendingEventsStatusesException extends RuntimeException {
    public CanNotUpdatePendingEventsStatusesException(String message) {
        super(message);
    }
}