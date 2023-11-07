package ru.practicum.ewm.event.participation.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestDto;
import ru.practicum.ewm.event.participation.request.service.EventParticipationRequestService;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

import static ru.practicum.ewm.event.participation.request.util.EventParticipationRequestMapper.*;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventParticipationRequestController {
    private final EventParticipationRequestService requestService;
    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    public List<EventParticipationRequestDto> getUsersRequests(@PathVariable Long userId) {
        log.info("Request on getting own event participation requests from user with id={} has been received", userId);
        userService.getUserById(userId);
        return toEventParticipationRequestDtos(requestService.getUsersRequests(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Request on posting event participation request from user with id={} for event with id={} " +
                "has been received", userId, eventId);
        User requester = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        return toEventParticipationRequestDto(requestService.createRequest(toEventParticipationRequest(event, requester)));
    }

    @PatchMapping("/{requestId}/cancel")
    public EventParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Request on canceling own event participation request from user with id={} for event with id={} " +
                "has been received", userId, requestId);
        userService.getUserById(userId);
        return toEventParticipationRequestDto(requestService.cancelRequestByRequester(requestId, userId));
    }
}