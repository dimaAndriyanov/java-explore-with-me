package ru.practicum.ewm.event.moderation.issue.service;

import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueResolveDto;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssue;

import java.util.List;
import java.util.Map;

public interface EventModerationIssueService {
    EventModerationIssue createEventModerationIssue(EventModerationIssue issue);

    List<EventModerationIssue> getUsersEventModerationIssues(Long userId, Boolean onlyOpened);

    List<EventModerationIssue> getUsersEventModerationIssuesByEventId(Long userId, Long eventId, Boolean onlyOpened);

    EventModerationIssue replyToEventModerationIssue(Long issueId, Long userId, String reply);

    List<EventModerationIssue> getEventModerationIssues(Boolean onlyReplied, int from, int size);

    List<EventModerationIssue> resolveRepliedEventModerationIssues(Map<Long, EventModerationIssueResolveDto> resolves);
}