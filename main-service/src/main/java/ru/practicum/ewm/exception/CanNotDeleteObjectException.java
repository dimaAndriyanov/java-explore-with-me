package ru.practicum.ewm.exception;

public class CanNotDeleteObjectException extends RuntimeException {
    public CanNotDeleteObjectException(String message) {
        super(message);
    }
}