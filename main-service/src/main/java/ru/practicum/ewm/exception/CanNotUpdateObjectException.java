package ru.practicum.ewm.exception;

public class CanNotUpdateObjectException extends RuntimeException {
    public CanNotUpdateObjectException(String message) {
        super(message);
    }
}