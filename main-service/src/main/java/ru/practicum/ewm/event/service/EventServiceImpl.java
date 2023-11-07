package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.CouldNotGetStatsException;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;
import ru.practicum.ewm.event.participation.request.model.Status;
import ru.practicum.ewm.event.participation.request.repository.EventParticipationRequestRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.CanNotUpdateObjectException;
import ru.practicum.ewm.exception.CanNotUpdatePublishedEventException;
import ru.practicum.ewm.exception.CouldNotReadStatServerResponseException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventParticipationRequestRepository requestRepository;
    private final StatClient statClient;

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByUserId(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Long> eventsIds = eventRepository.findAllEventsIdsByInitiatorId(userId, pageRequest).getContent();
        List<Event> events = eventRepository.findAllByIdEager(eventsIds);

        try {
            return mapViewsToEvents(events);
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return events;
        }
    }

    @Override
    @Transactional
    public Event createEvent(Event event) {
        eventRepository.save(event);
        log.info("Event\n{}\nhas been created", event);
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventByIdAndUserId(Long eventId, Long userId) {
        Event result;
        try {
            result = eventRepository.findByIdAndInitiatorId(eventId, userId).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        try {
            return mapViewsToEvent(result);
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return result;
        }
    }

    @Override
    @Transactional
    public Event updateEventByUser(Event updatedEvent, Long eventId, Long userId, StateAction stateAction) {
        Event savedEvent;
        try {
            savedEvent = eventRepository.findByIdAndInitiatorId(eventId, userId).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        if (savedEvent.getState().equals(State.PUBLISHED)) {
            throw new CanNotUpdatePublishedEventException("Users can not update already published event");
        }

        State state;
        if (stateAction == null) {
            state = savedEvent.getState();
        } else {
            state = stateAction.equals(StateAction.SEND_TO_REVIEW) ? State.PENDING : State.CANCELED;
        }
        Event eventToUpdate = getEventToUpdate(updatedEvent, savedEvent, state, false);
        if (eventToUpdate.getEventDate().isBefore(eventToUpdate.getCreatedOn().plusHours(2))) {
            throw new CanNotUpdateObjectException("Changes can be applied at least 2 hours before event date");
        }
        eventRepository.saveAndFlush(eventToUpdate);

        log.info("Event\n{}\nhas been updated by user with id={}", eventToUpdate, userId);

        try {
            return mapViewsToEvent(eventToUpdate);
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return eventToUpdate;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(GetEventsRequestParameters parameters) {
        PageRequest pageRequest;
        if (parameters.getSort() != null && parameters.getSort().equals(Sorts.EVENT_DATE)) {
            pageRequest = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize(), Sort.by(Sort.Direction.ASC, "eventDate"));
        } else {
            pageRequest = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());
        }
        BooleanExpression filters = getFilters(parameters);
        List<Long> eventsIds;
        if (filters == null) {
            eventsIds = eventRepository.findAllEventsIds(pageRequest).getContent();
        } else {
            eventsIds = eventRepository.findAll(filters, pageRequest).getContent()
                    .stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
        }
        List<Event> result = eventRepository.findAllByIdEager(eventsIds);

        try {
            mapViewsToEvents(result);
            if (parameters.getSort() != null && parameters.getSort().equals(Sorts.VIEWS)) {
                result.sort(Comparator.comparingDouble(Event::getViews).reversed());
            }
            return result;
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return result;
        }
    }

    @Override
    @Transactional
    public Event updateEventByAdmin(Event updatedEvent, Long eventId, StateAction stateAction) {
        Event savedEvent;
        try {
            savedEvent = eventRepository.findByIdEager(eventId).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }

        State state;
        boolean confirmAllPendingRequests = false;
        boolean rejectAllPendingRequests = false;
        if (stateAction == null) {
            state = savedEvent.getState();
            if (state.equals(State.PUBLISHED)) {
                if (updatedEvent.getParticipantLimit() != null) {
                    if (updatedEvent.getParticipantLimit() == 0) {
                        boolean requestModeration = updatedEvent.getRequestModeration() != null ?
                                updatedEvent.getRequestModeration() : savedEvent.getRequestModeration();
                        if (!requestModeration) {
                            confirmAllPendingRequests = true;
                        }
                    } else if (updatedEvent.getParticipantLimit() < savedEvent.getConfirmedRequests()) {
                        throw new CanNotUpdateObjectException("Participant limit of updated event can not be less " +
                                "than already confirmed requests amount");
                    } else if (updatedEvent.getParticipantLimit().equals(savedEvent.getConfirmedRequests())) {
                        rejectAllPendingRequests = true;
                    }
                }
                if (updatedEvent.getRequestModeration() != null) {
                    if (!updatedEvent.getRequestModeration()) {
                        int participantLimit = updatedEvent.getParticipantLimit() != null ?
                                updatedEvent.getParticipantLimit() : savedEvent.getParticipantLimit();
                        if (participantLimit == 0) {
                            confirmAllPendingRequests = true;
                        } else {
                            int vacantSpacesAmount = participantLimit - savedEvent.getConfirmedRequests();
                            int pendingRequestsAmount = (int) getPendingRequestsAmountForEvent(eventId);
                            if (vacantSpacesAmount < pendingRequestsAmount) {
                                throw new CanNotUpdateObjectException("Can not set request moderation to false " +
                                        "if vacant spaces amount is less than pending requests amount");
                            } else {
                                confirmAllPendingRequests = true;
                            }
                        }
                    }
                }
            }
        } else {
            state = stateAction.equals(StateAction.PUBLISH_EVENT) ? State.PUBLISHED : State.CANCELED;
            if (state.equals(State.PUBLISHED) && !savedEvent.getState().equals(State.PENDING)) {
                throw new CanNotUpdateObjectException("Can publish ony pending events");
            }
            if (state.equals(State.CANCELED) && savedEvent.getState().equals(State.PUBLISHED)) {
                throw new CanNotUpdateObjectException("Can not cancel published event");
            }
        }

        Event eventToUpdate = getEventToUpdate(updatedEvent, savedEvent, state, true);
        if (eventToUpdate.getState().equals(State.PUBLISHED) &&
                eventToUpdate.getEventDate().isBefore(eventToUpdate.getPublishedOn().plusHours(1))) {
            throw new CanNotUpdateObjectException("Publishing can be applied at least 1 hour before event date");
        }

        if (confirmAllPendingRequests) {
            confirmAllPendingRequestsForEvent(eventToUpdate);
        }
        if (rejectAllPendingRequests) {
            rejectAllPendingRequestsForEvent(eventToUpdate);
        }
        eventRepository.saveAndFlush(eventToUpdate);
        log.info("Event\n{}\nhas been updated by Admin", eventToUpdate);

        try {
            return mapViewsToEvent(eventToUpdate);
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return eventToUpdate;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        try {
            return eventRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event with id=" + id + " was not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventParticipationRequest> getRequestsForUsersEvent(Long userId, Long eventId) {
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isEmpty()) {
            throw new ObjectNotFoundException("Event with id =" + eventId + " was not found");
        }
        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    @Transactional
    public List<EventParticipationRequest> updateRequestsStatusForUsersEvent(
            Long userId, Long eventId, EventParticipationRequestStatusUpdateRequest updateRequest) {
        Event event;
        try {
            event = eventRepository.findByIdAndInitiatorId(eventId, userId).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event with id =" + eventId + " was not found");
        }
        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            return List.of();
        }
        if (event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests().equals(event.getParticipantLimit()) &&
                updateRequest.getStatus().equals(Status.CONFIRMED)) {
            throw new CanNotUpdateObjectException("Can not confirm requests for event that reached its participation limit");
        }

        List<EventParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        Optional<Long> possiblyNotFoundRequest = requests.stream()
                .filter(request -> !request.getEvent().getId().equals(eventId))
                .map(EventParticipationRequest::getId)
                .findAny();
        if (possiblyNotFoundRequest.isPresent()) {
            throw new ObjectNotFoundException("Request with id=" + possiblyNotFoundRequest.get() + " was not found");
        }
        Optional<Long> possiblyNotPendingRequest = requests.stream()
                .filter(request -> !request.getStatus().equals(Status.PENDING))
                .map(EventParticipationRequest::getId)
                .findAny();
        if (possiblyNotPendingRequest.isPresent()) {
            throw new CanNotUpdateObjectException("Can not update status of request with id=" +
                    possiblyNotPendingRequest.get() + " due to its status is not PENDING");
        }
        if (updateRequest.getStatus().equals(Status.CONFIRMED) &&
                event.getConfirmedRequests() + requests.size() > event.getParticipantLimit()) {
            throw new CanNotUpdateObjectException("Can not confirm requested event participation requests due to " +
                    "amount of confirmed requests will exceed participation limit for event");
        }

        requests.forEach(request -> request.setStatus(updateRequest.getStatus()));
        if (updateRequest.getStatus().equals(Status.CONFIRMED)) {
            event.increaseConfirmedRequestsBy(requests.size());
        }
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            rejectAllPendingRequestsForEvent(event);
        }
        requestRepository.saveAll(requests);
        eventRepository.saveAndFlush(event);
        log.info("Status of event participation requests with id in ({}) has been updated to {}",
                updateRequest.getRequestIds(), updateRequest.getStatus());

        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getPublishedEventById(Long id) {
        Event result;
        try {
            result = eventRepository.findByIdAndState(id, State.PUBLISHED).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event with id=" + id + " was not found");
        }
        try {
            return mapViewsToEvent(result);
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return result;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsById(Iterable<Long> ids) {
        List<Event> result = eventRepository.findAllByIdEager(ids);
        try {
            return mapViewsToEvents(result);
        } catch (CouldNotReadStatServerResponseException exception) {
            log.warn("Could not read response from stat server\n{}", exception.getMessage());
            return result;
        }
    }

    private List<Event> mapViewsToEvents(List<Event> events) {
        try {
            Map<Long, Long> stats = statClient.getStats(
                            LocalDateTime.of(0, 1, 1, 0, 0),
                            LocalDateTime.of(9999, 12, 31, 0, 0),
                            events.stream().map(event -> "/events/" + event.getId()).collect(Collectors.toList()),
                            true)
                    .stream()
                    .collect(Collectors.toMap(this::getEventId, ViewStatsDto::getHits));
            events.forEach(event -> event.setViews(stats.getOrDefault(event.getId(), 0L)));
            return events;
        } catch (CouldNotGetStatsException exception) {
            throw new CouldNotReadStatServerResponseException(exception.getMessage());
        }
    }

    private Event mapViewsToEvent(Event event) {
        try {
            Optional<ViewStatsDto> stat = statClient.getStats(
                    LocalDateTime.of(0, 1, 1, 0, 0),
                    LocalDateTime.of(9999, 12, 31, 0, 0),
                    List.of("/events/" + event.getId()),
                    true
            ).stream().findAny();
            if (stat.isPresent()) {
                event.setViews(stat.get().getHits());
            } else {
                event.setViews(0L);
            }
            return event;
        } catch (CouldNotGetStatsException exception) {
            throw new CouldNotReadStatServerResponseException(exception.getMessage());
        }
    }


    private Long getEventId(ViewStatsDto stat) {
        String[] uriParts = stat.getUri().split("/");
        if (uriParts.length != 3 || !(uriParts[1].equals("events"))) {
            throw new CouldNotReadStatServerResponseException("Received stat.uri=" + stat.getUri() + " and does not have pattern /events/{id}");
        }
        try {
            return Long.parseLong(uriParts[2]);
        } catch (NumberFormatException exception) {
            throw new CouldNotReadStatServerResponseException("Received stat.uri=" + stat.getUri() + " and does not have pattern /events/{id}");
        }
    }

    private Event getEventToUpdate(Event updatedEvent, Event savedEvent, State state, boolean byAdmin) {
        return new Event(
                savedEvent.getId(),
                updatedEvent.getTitle() != null && !updatedEvent.getTitle().isBlank() ? updatedEvent.getTitle() : savedEvent.getTitle(),
                updatedEvent.getAnnotation() != null && !updatedEvent.getAnnotation().isBlank() ? updatedEvent.getAnnotation() : savedEvent.getAnnotation(),
                updatedEvent.getDescription() != null && ! updatedEvent.getDescription().isBlank() ? updatedEvent.getDescription() : savedEvent.getDescription(),
                updatedEvent.getEventDate() != null ? updatedEvent.getEventDate() : savedEvent.getEventDate(),
                updatedEvent.getLocationLat() != null ? updatedEvent.getLocationLat() : savedEvent.getLocationLat(),
                updatedEvent.getLocationLon() != null ? updatedEvent.getLocationLon() : savedEvent.getLocationLon(),
                updatedEvent.getPaid() != null ? updatedEvent.getPaid() : savedEvent.getPaid(),
                updatedEvent.getParticipantLimit() != null ? updatedEvent.getParticipantLimit() : savedEvent.getParticipantLimit(),
                updatedEvent.getRequestModeration() != null ? updatedEvent.getRequestModeration() : savedEvent.getRequestModeration(),
                state,
                byAdmin ? savedEvent.getCreatedOn() : LocalDateTime.now(),
                state.equals(State.PUBLISHED) ? LocalDateTime.now() : null,
                savedEvent.getInitiator(),
                updatedEvent.getCategory() != null ? updatedEvent.getCategory() : savedEvent.getCategory(),
                savedEvent.getConfirmedRequests(),
                0L
        );
    }

    private BooleanExpression getFilters(GetEventsRequestParameters parameters) {
        List<BooleanExpression> filters = new ArrayList<>();
        if (parameters.getUsers() != null && !parameters.getUsers().isEmpty()) {
            System.out.println(1);
            filters.add(QEvent.event.initiator.id.in(parameters.getUsers()));
        }
        if (parameters.getStates() != null && !parameters.getStates().isEmpty()) {
            System.out.println(2);
            filters.add(QEvent.event.state.in(parameters.getStates()));
        }
        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            System.out.println(3);
            filters.add(QEvent.event.category.id.in(parameters.getCategories()));
        }
        if (parameters.getRangeStart() != null) {
            System.out.println(4);
            filters.add(QEvent.event.eventDate.after(parameters.getRangeStart()));
        }
        if (parameters.getRangeEnd() != null) {
            System.out.println(5);
            filters.add(QEvent.event.eventDate.before(parameters.getRangeEnd()));
        }
        if (parameters.getText() != null && !parameters.getText().isBlank()) {
            String text = "%" + parameters.getText() + "%";
            System.out.println(6);
            filters.add(
                    QEvent.event.annotation.likeIgnoreCase(text).or(
                            QEvent.event.description.likeIgnoreCase(text)
                    ));
        }
        if (parameters.getPaid() != null) {
            System.out.println(7);
            filters.add(QEvent.event.paid.eq(parameters.getPaid()));
        }
        if (parameters.getOnlyAvailable()) {
            System.out.println(8);
            filters.add(
                    QEvent.event.participantLimit.eq(0).or(
                            QEvent.event.participantLimit.subtract(QEvent.event.confirmedRequests).gt(0)
                    ));
        }
        if (filters.isEmpty()) {
            System.out.println(9);
            return null;
        } else {
            System.out.println(10);
            System.out.println(filters.size());
            BooleanExpression result = filters.get(0);
            for (int i = 1; i < filters.size(); i++) {
                result = result.and(filters.get(i));
            }
            return result;
        }
    }

    private void confirmAllPendingRequestsForEvent(Event event) {
        List<EventParticipationRequest> requests = requestRepository.findAllByEventIdAndStatus(event.getId(), Status.PENDING);
        requests.forEach(request -> request.setStatus(Status.CONFIRMED));
        event.increaseConfirmedRequestsBy(requests.size());
        requestRepository.saveAll(requests);
    }

    private void rejectAllPendingRequestsForEvent(Event event) {
        List<EventParticipationRequest> requests = requestRepository.findAllByEventIdAndStatus(event.getId(), Status.PENDING);
        requests.forEach(request -> request.setStatus(Status.REJECTED));
        requestRepository.saveAll(requests);
    }

    private long getPendingRequestsAmountForEvent(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, Status.PENDING);
    }
}