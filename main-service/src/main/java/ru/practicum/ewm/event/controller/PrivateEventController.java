package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestDto;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.event.participation.request.dto.EventParticipationRequestStatusUpdateResponse;
import ru.practicum.ewm.event.participation.request.model.Status;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.valid.OnCreate;
import ru.practicum.ewm.valid.OnUpdateByUser;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.event.util.EventMapper.*;
import static ru.practicum.ewm.event.participation.request.util.EventParticipationRequestMapper.*;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping
    public List<EventShortDto> getEventsByUserId(@PathVariable Long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Request on getting users events from user with id={} with parameters from={} and size={} has been received",
                userId, from, size);
        userService.getUserById(userId);
        return toEventShortDtos(eventService.getEventsByUserId(userId, from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnCreate.class)
    public EventDto createEventByUserId(@PathVariable Long userId,
                                 @Valid @RequestBody EventRequestDto eventRequestDto) {
        log.info("Request on posting event with\nbody={}\nfrom user with id={} has been received", eventRequestDto, userId);
        User initiator = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(eventRequestDto.getCategory());
        return toEventDto(eventService.createEvent(toEvent(eventRequestDto, initiator, category, true)));
    }

    @GetMapping("/{eventId}")
    public EventDto getEventByIdAndUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Request on getting users event with id={} from user with id={} has been received", eventId, userId);
        userService.getUserById(userId);
        return toEventDto(eventService.getEventByIdAndUserId(eventId, userId));
    }

    @PatchMapping("/{eventId}")
    @Validated(OnUpdateByUser.class)
    public EventDto updateEventByUser(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody EventRequestDto eventRequestDto) {
        log.info("Request on updating event with id={} and\nbody=\n{}\nfrom user with id={} has been received",
                eventId, eventRequestDto, userId);
        userService.getUserById(userId);
        Category category = eventRequestDto.getCategory() == null ? null :
                categoryService.getCategoryById(eventRequestDto.getCategory());
        return toEventDto(eventService.updateEventByUser(
                toEvent(eventRequestDto, null, category, false), eventId, userId, eventRequestDto.getStateAction()));
    }

    @GetMapping("/{eventId}/requests")
    public List<EventParticipationRequestDto> getRequestsForUsersEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        userService.getUserById(userId);
        log.info("Request on getting event participation requests for own event with id={} from user with id={} " +
                "has been received", eventId, userId);
        return toEventParticipationRequestDtos(eventService.getRequestsForUsersEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public EventParticipationRequestStatusUpdateResponse updateRequestsStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventParticipationRequestStatusUpdateRequest updateRequest
            ) {
        log.info("Request on changing status for {} of event participation requests with id in {} for event with id={} " +
                "from user with id={} has been received", updateRequest.getStatus().name(), updateRequest.getRequestIds(),
                eventId, userId);
        return toEventParticipationRequestStatusUpdateResponse(
                eventService.updateRequestsStatusForUsersEvent(userId, eventId, updateRequest),
                updateRequest.getStatus().equals(Status.CONFIRMED)
        );
    }
}