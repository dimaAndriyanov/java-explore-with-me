package ru.practicum.ewm.event.moderation.issue.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueDto;
import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueResolveDto;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssue;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssueStatus;
import ru.practicum.ewm.exception.NotUniqueObjectsIdsInListException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class EventModerationIssueMapper {
    public EventModerationIssue toEventModerationIssue(Event event, String message) {
        return new EventModerationIssue(
                null,
                event,
                message,
                EventModerationIssueStatus.OPENED,
                null
        );
    }

    public EventModerationIssueDto toEventModerationIssueDto(EventModerationIssue issue) {
        return new EventModerationIssueDto(
                issue.getId(),
                issue.getEvent().getId(),
                issue.getIssueMessage(),
                issue.getIssueStatus(),
                issue.getUsersReply()
        );
    }

    public List<EventModerationIssueDto> toEventModerationIssueDtos(List<EventModerationIssue> issues) {
        return issues
                .stream()
                .map(EventModerationIssueMapper::toEventModerationIssueDto)
                .collect(Collectors.toList());
    }

    public Map<Long, EventModerationIssueResolveDto> toIssueIdsToIssueResolveMap(List<EventModerationIssueResolveDto> resolves) {
        Map<Long, EventModerationIssueResolveDto> result = resolves
                .stream()
                .collect(Collectors.toMap(EventModerationIssueResolveDto::getIssueId, resolve -> resolve));
        if (result.size() != resolves.size()) {
            throw new NotUniqueObjectsIdsInListException("Issue ids must be unique in list");
        }
        return result;
    }
}