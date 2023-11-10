package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.GetEventsRequestParameters;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {
    List<Event> getEventsByUserId(Long userId, int from, int size);

    Event createEvent(Event event);

    Event getEventByIdAndUserId(Long eventId, Long userId);

    Event updateEventByUser(Event updatedEvent, Long eventId, Long userId, StateAction stateAction);

    List<Event> getEvents(GetEventsRequestParameters parameters);

    Event updateEventByAdmin(Event updatedEvent, Long eventId, StateAction stateAction);

    Event getEventById(Long eventId);

    List<EventParticipationRequest> getRequestsForUsersEvent(Long userId, Long eventId);

    List<EventParticipationRequest> updateRequestsStatusForUsersEvent(
            Long userId, Long eventId, EventParticipationRequestStatusUpdateRequest updateRequest);

    Event getPublishedEventById(Long id);

    List<Event> getEventsById(Iterable<Long> ids);

    List<Event> cancelAllHappenedPendingEvents(LocalDateTime time);

    List<Event> updatePendingEventsStatuses(Map<Long, State> eventIdsToStates);
}