package ru.practicum.ewm;

public class CouldNotSaveHitException extends RuntimeException {
    public CouldNotSaveHitException(String message) {
        super(message);
    }
}