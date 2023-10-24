package ru.practicum.ewm;

public class CouldNotGetStatsException extends RuntimeException {
    public CouldNotGetStatsException(String message) {
        super(message);
    }
}