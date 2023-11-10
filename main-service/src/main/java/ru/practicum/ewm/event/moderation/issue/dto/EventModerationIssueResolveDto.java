package ru.practicum.ewm.event.moderation.issue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssueStatus;
import ru.practicum.ewm.valid.ValidEventModerationIssueStatusForResolve;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(force = true)
public class EventModerationIssueResolveDto {
    @NotNull
    private final Long issueId;

    @Size(min = 6, max = 1024)
    private final String message;

    @ValidEventModerationIssueStatusForResolve
    private final EventModerationIssueStatus issueStatus;
}