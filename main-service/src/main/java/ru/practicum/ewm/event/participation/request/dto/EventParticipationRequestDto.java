package ru.practicum.ewm.event.participation.request.dto;

import lombok.Data;
import ru.practicum.ewm.event.participation.request.model.Status;

import java.time.LocalDateTime;

@Data
public class EventParticipationRequestDto {
    private final Long id;
    private final LocalDateTime created;
    private final Status status;
    private final Long event;
    private final Long requester;
}