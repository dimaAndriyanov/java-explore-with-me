package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.ModerationReplyDto;
import ru.practicum.ewm.event.model.GetEventsRequestParameters;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotValidRequestParametersException;
import ru.practicum.ewm.valid.OnUpdateByAdmin;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.event.util.EventMapper.*;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {
    private final EventService eventService;
    private final CategoryService categoryService;

    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) List<State> states,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Request on getting events with parameters:\nusers={}\nstates={}\ncategories={}" +
                "\nrangeStart={}\nrangeEnd={}\nfrom={}\nsize={}\nhas been received",
                users, states, categories, rangeStart, rangeEnd, from, size);
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new NotValidRequestParametersException("rangeStart should be before than rangeEnd");
        }
        return toEventDtos(eventService.getEvents(new GetEventsRequestParameters(
                users, states, categories, rangeStart, rangeEnd, from, size,null, null, false, null)
        ));
    }

    @PatchMapping("/{eventId}")
    @Validated(OnUpdateByAdmin.class)
    public EventDto updateEventByAdmin(@PathVariable Long eventId,
                                @Valid @RequestBody EventRequestDto eventRequestDto) {
        log.info("Request on updating event with id={} and\nbody={}\nfrom Admin has been received", eventId, eventRequestDto);
        Category category = eventRequestDto.getCategory() == null ? null :
                categoryService.getCategoryById(eventRequestDto.getCategory());
        return toEventDto(eventService.updateEventByAdmin(
                toEvent(eventRequestDto, null, category, false), eventId, eventRequestDto.getStateAction()
        ));
    }

    @GetMapping("/pending")
    public List<EventDto> getPendingEvents(@Min (3900) @RequestParam(defaultValue = "7200") Long remainingTime,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Request on getting pending events with parameters remainingTime={}, from={} and size={} has been received",
                remainingTime, from, size);
        return toEventDtos(eventService.getEvents(new GetEventsRequestParameters(
                null, List.of(State.PENDING), null, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusSeconds(remainingTime), from, size, null, null, null, null
        )));
    }

    @PatchMapping("/pending/happened/cancel")
    public List<EventDto> cancelAllHappenedPendingEvents() {
        log.info("Request on canceling all happened pending events has been received");
        return toEventDtos(eventService.cancelAllHappenedPendingEvents(LocalDateTime.now().plusHours(1)));
    }

    @PatchMapping("/pending/reply")
    public List<EventDto> updatePendingEventsStatuses(@Valid @RequestBody List<ModerationReplyDto> replies) {
        log.info("Request on changing statuses of pending events\nwith body ={}\nhas been received", replies);
        return toEventDtos(eventService.updatePendingEventsStatuses(toEventToStateMap(replies)));
    }
}