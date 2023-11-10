package ru.practicum.ewm.event.moderation.issue.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueDto;
import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueResolveDto;
import ru.practicum.ewm.event.moderation.issue.service.EventModerationIssueService;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.CanNotCreateIssueToEvent;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import java.util.List;

import static ru.practicum.ewm.event.moderation.issue.util.EventModerationIssueMapper.*;

@RestController
@RequestMapping("admin/events/issues")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventModerationIssueController {
    private final EventModerationIssueService issueService;
    private final EventService eventService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EventModerationIssueDto createEventModerationIssue(
            @PathVariable Long eventId,
            @NotBlank @Size(min = 6, max = 1024) @RequestParam String message) {
        log.info("Request on creating event moderation issue to event with id={}, and message={} has been received",
                eventId, message);
        Event event = eventService.getEventById(eventId);
        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CHANGE_REQUIRED)) {
            throw new CanNotCreateIssueToEvent("Can create event moderation issue only to events " +
                    "with status PENDING or CHANGERS_REQUIRED");
        }
        return toEventModerationIssueDto(issueService.createEventModerationIssue(
                toEventModerationIssue(event, message)
        ));
    }

    @GetMapping
    public List<EventModerationIssueDto> getEventModerationIssues(@RequestParam(defaultValue = "true") Boolean onlyReplied,
                                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Request on getting issues with params onlyReplied={}, from={} and size={} has been received",
                onlyReplied, from, size);
        return toEventModerationIssueDtos(issueService.getEventModerationIssues(onlyReplied, from, size));
    }

    @PatchMapping
    public List<EventModerationIssueDto> resolveRepliedEventModerationIssues(
            @Valid @RequestBody List<EventModerationIssueResolveDto> resolves) {
        log.info("Request on updating REPLIED event moderation issues with\nbody={}\nhas been received", resolves);
        return toEventModerationIssueDtos((issueService.resolveRepliedEventModerationIssues(
                toIssueIdsToIssueResolveMap(resolves))));
    }
}