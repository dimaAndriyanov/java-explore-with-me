package ru.practicum.ewm.event.participation.request.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestDto;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestStatusUpdateResponse;
import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;
import ru.practicum.ewm.event.participation.request.model.Status;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventParticipationRequestMapper {
    public EventParticipationRequest toEventParticipationRequest(Event event, User requester) {
        return new EventParticipationRequest(
                null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                Status.PENDING,
                event,
                requester
        );
    }

    public EventParticipationRequestDto toEventParticipationRequestDto(EventParticipationRequest request) {
        return new EventParticipationRequestDto(
                request.getId(),
                request.getCreated(),
                request.getStatus(),
                request.getEvent().getId(),
                request.getRequester().getId()
        );
    }

    public List<EventParticipationRequestDto> toEventParticipationRequestDtos(List<EventParticipationRequest> requests) {
        return requests
                .stream()
                .map(EventParticipationRequestMapper::toEventParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public EventParticipationRequestStatusUpdateResponse toEventParticipationRequestStatusUpdateResponse(
            List<EventParticipationRequest> requests,
            boolean confirmed
    ) {
        List<EventParticipationRequestDto> requestDtos = toEventParticipationRequestDtos(requests);
        return confirmed ? new EventParticipationRequestStatusUpdateResponse(requestDtos, List.of()) :
                new EventParticipationRequestStatusUpdateResponse(List.of(), requestDtos);
    }
}