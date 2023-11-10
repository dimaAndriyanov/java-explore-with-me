package ru.practicum.ewm.exception;

public class CanNotCreateIssueToEvent extends RuntimeException {
    public CanNotCreateIssueToEvent(String message) {
        super(message);
    }
}