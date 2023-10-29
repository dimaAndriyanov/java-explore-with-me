package ru.practicum.ewm.exception;

public class NotIpAddressException extends RuntimeException {
    public NotIpAddressException(String message) {
        super(message);
    }
}