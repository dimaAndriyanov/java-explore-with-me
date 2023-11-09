package ru.practicum.ewm.exception;

public class CouldNotReadStatServerResponseException extends RuntimeException {
    public CouldNotReadStatServerResponseException(String message) {
        super(message);
    }
}