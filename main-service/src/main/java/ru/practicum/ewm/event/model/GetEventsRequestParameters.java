package ru.practicum.ewm.event.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetEventsRequestParameters {
    private final List<Long> users;
    private final List<State> states;
    private final List<Long> categories;
    private final LocalDateTime rangeStart;
    private final LocalDateTime rangeEnd;
    private final int from;
    private final int size;
    private final String text;
    private final Boolean paid;
    private final Boolean onlyAvailable;
    private final Sorts sort;
}