package ru.practicum.ewm.exception;

public class CanNotCreateEventParticipationException extends RuntimeException {
    public CanNotCreateEventParticipationException(String message) {
        super(message);
    }
}