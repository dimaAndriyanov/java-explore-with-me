package ru.practicum.ewm.event.moderation.issue.dto;

import lombok.Data;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssueStatus;

@Data
public class EventModerationIssueDto {
    private final Long id;
    private final Long eventId;
    private final String issue;
    private final EventModerationIssueStatus status;
    private final String reply;
}