package ru.practicum.ewm.event.participation.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;
import ru.practicum.ewm.event.participation.request.model.Status;
import ru.practicum.ewm.event.participation.request.repository.EventParticipationRequestRepository;
import ru.practicum.ewm.exception.CanNotCreateEventParticipationException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationRequestServiceImpl implements EventParticipationRequestService {
    private final EventParticipationRequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventParticipationRequest> getUsersRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    @Transactional
    public EventParticipationRequest createRequest(EventParticipationRequest request) {
        validateEventParticipationRequest(request);
        if (!request.getEvent().getRequestModeration() || request.getEvent().getParticipantLimit() == 0) {
            request.getEvent().increaseConfirmedRequestsBy(1);
            request.setStatus(Status.CONFIRMED);
        }
        requestRepository.saveAndFlush(request);
        log.info("Event participation request\n{}\nhas been created", request);

        return request;
    }

    @Override
    @Transactional
    public EventParticipationRequest cancelRequestByRequester(Long requestId, Long userId) {
        EventParticipationRequest request;
        try {
            request = requestRepository.findByIAndRequesterIddWithEvent(requestId, userId).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Event participation request with id=" + requestId + " was not found");
        }
        if (request.getStatus().equals(Status.CONFIRMED)) {
            request.getEvent().decreaseConfirmedRequestsBy(1);
        }
        request.setStatus(Status.CANCELED);
        requestRepository.saveAndFlush(request);
        log.info("Event participation request\n{}\nhas been canceled by user with id={}", request, userId);

        return request;
    }

    private void validateEventParticipationRequest(EventParticipationRequest request) {
        if (requestRepository.findByRequesterIdAndEventId(
                request.getRequester().getId(), request.getEvent().getId()).isPresent()) {
            throw new CanNotCreateEventParticipationException(
                    "Can not create another event participation request if one already exists");
        }
        if (request.getRequester().getId().equals(request.getEvent().getInitiator().getId())) {
            throw new CanNotCreateEventParticipationException(
                    "Can not create event participation request for own event");
        }
        if (!request.getEvent().getState().equals(State.PUBLISHED)) {
            throw new CanNotCreateEventParticipationException(
                    "Can not create event participation request for not published event");
        }
        if (request.getEvent().getParticipantLimit() != 0 &&
                request.getEvent().getConfirmedRequests().equals(request.getEvent().getParticipantLimit())) {
            throw new CanNotCreateEventParticipationException(
                    "Can not create event participation request event that reached its participant limit");
        }
    }
}