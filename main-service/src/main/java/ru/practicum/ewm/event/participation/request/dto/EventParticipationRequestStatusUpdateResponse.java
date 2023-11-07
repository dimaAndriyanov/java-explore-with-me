package ru.practicum.ewm.event.participation.request.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventParticipationRequestStatusUpdateResponse {
    private final List<EventParticipationRequestDto> confirmedRequests;
    private final List<EventParticipationRequestDto> rejectedRequests;
}