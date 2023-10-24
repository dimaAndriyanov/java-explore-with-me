package ru.practicum.ewm.exception;

public class NoStatsForSuchApplicationException extends RuntimeException {
    public NoStatsForSuchApplicationException(String message) {
        super(message);
    }
}