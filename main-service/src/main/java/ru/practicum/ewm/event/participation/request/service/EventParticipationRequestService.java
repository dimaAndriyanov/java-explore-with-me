package ru.practicum.ewm.event.participation.request.service;

import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;

import java.util.List;

public interface EventParticipationRequestService {
    List<EventParticipationRequest> getUsersRequests(Long userId);

    EventParticipationRequest createRequest(EventParticipationRequest request);

    EventParticipationRequest cancelRequestByRequester(Long requestId, Long userId);
}