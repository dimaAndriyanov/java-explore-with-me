package ru.practicum.ewm.exception;

public class NotValidRequestParametersException extends RuntimeException {
    public NotValidRequestParametersException(String message) {
        super(message);
    }
}