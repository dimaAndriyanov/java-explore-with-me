package ru.practicum.ewm.exception;

public class NotUniqueObjectsIdsInListException extends RuntimeException {
    public NotUniqueObjectsIdsInListException(String message) {
        super(message);
    }
}