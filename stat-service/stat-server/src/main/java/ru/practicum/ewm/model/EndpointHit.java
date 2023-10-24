package ru.practicum.ewm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class EndpointHit {
    private final Long id;
    private final String app;
    private final String uri;
    private final long ip;
    private final LocalDateTime timestamp;
}