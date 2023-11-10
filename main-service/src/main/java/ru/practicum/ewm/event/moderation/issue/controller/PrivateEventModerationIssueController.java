package ru.practicum.ewm.event.moderation.issue.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueDto;
import ru.practicum.ewm.event.moderation.issue.service.EventModerationIssueService;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static ru.practicum.ewm.event.moderation.issue.util.EventModerationIssueMapper.*;

@RestController
@RequestMapping("/users/{userId}/events/issues")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventModerationIssueController {
    private final EventModerationIssueService issueService;
    private final UserService userService;
    private final EventService eventService;

    @GetMapping
    public List<EventModerationIssueDto> getUsersEventModerationIssues(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "true") Boolean onlyOpened) {
        log.info("Request on getting users event moderation issues from user with id={} with parameter onlyOpened={} " +
                "has been received", userId, onlyOpened);
        userService.getUserById(userId);
        return toEventModerationIssueDtos(issueService.getUsersEventModerationIssues(userId, onlyOpened));
    }

    @GetMapping("/{eventId}")
    public List<EventModerationIssueDto> getUsersEventModerationIssuesByEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "true") Boolean onlyOpened) {
        log.info("Request on getting users event moderation issues from user with id={} with parameter onlyOpened={} " +
                "has been received", userId, onlyOpened);
        userService.getUserById(userId);
        eventService.getEventById(eventId);
        return toEventModerationIssueDtos(issueService.getUsersEventModerationIssuesByEventId(userId, eventId, onlyOpened));
    }

    @PatchMapping("/{issueId}")
    public EventModerationIssueDto replyToEventModerationIssue(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @NotBlank @Size(min = 6, max = 1024) @RequestParam String reply) {
        log.info("Request on replying to issue with id={} from user with id={} and reply={} has been received",
                issueId, userId, reply);
        return toEventModerationIssueDto(issueService.replyToEventModerationIssue(issueId, userId, reply));
    }
}