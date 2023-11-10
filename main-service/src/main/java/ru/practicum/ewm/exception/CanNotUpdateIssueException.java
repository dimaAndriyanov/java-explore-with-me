package ru.practicum.ewm.exception;

public class CanNotUpdateIssueException extends RuntimeException {
    public CanNotUpdateIssueException(String message) {
        super(message);
    }
}