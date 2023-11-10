package ru.practicum.ewm.event.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.ModerationReplyDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.exception.NotUniqueObjectsIdsInListException;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.user.util.UserMapper.*;
import static ru.practicum.ewm.category.util.CategoryMapper.*;

@UtilityClass
public class EventMapper {
    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                event.getPaid(),
                toUserShortDto(event.getInitiator()),
                toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getViews()
        );
    }

    public List<EventShortDto> toEventShortDtos(List<Event> events) {
        return events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventDto toEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                event.getEventDate(),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getState(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                toUserShortDto(event.getInitiator()),
                toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getViews()
        );
    }

    public List<EventDto> toEventDtos(List<Event> events) {
        return events
                .stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());
    }

    public Event toEvent(EventRequestDto eventRequestDto, User initiator, Category category, boolean isBeingCreated) {
        Boolean paid = eventRequestDto.getPaid();
        Boolean requestModeration = eventRequestDto.getRequestModeration();
        Integer participantLimit = eventRequestDto.getParticipantLimit();
        if (isBeingCreated) {
            paid = paid == null ? false : paid;
            requestModeration = requestModeration == null ? true : requestModeration;
            participantLimit = participantLimit == null ? 0 : participantLimit;
        }
        return new Event(
                null,
                eventRequestDto.getTitle(),
                eventRequestDto.getAnnotation(),
                eventRequestDto.getDescription(),
                eventRequestDto.getEventDate(),
                eventRequestDto.getLocation() != null ? eventRequestDto.getLocation().getLat() : null,
                eventRequestDto.getLocation() != null ? eventRequestDto.getLocation().getLon() : null,
                paid,
                participantLimit,
                requestModeration,
                State.PENDING,
                LocalDateTime.now(),
                null,
                initiator,
                category,
                0,
                0L
        );
    }

    public Map<Long, State> toEventToStateMap(List<ModerationReplyDto> replies) {
        Map<Long, State> result = replies
                .stream()
                .collect(Collectors.toMap(ModerationReplyDto::getEventId,
                        reply -> reply.getAction().equals(StateAction.PUBLISH_EVENT) ? State.PUBLISHED : State.CANCELED));
        if (result.size() != replies.size()) {
            throw new NotUniqueObjectsIdsInListException("EventIds must be unique in list");
        }
        return result;
    }
}