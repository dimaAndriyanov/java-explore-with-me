package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.CouldNotSaveHitException;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.GetEventsRequestParameters;
import ru.practicum.ewm.event.model.Sorts;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotValidRequestParametersException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.practicum.ewm.event.util.EventMapper.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {
    private final EventService eventService;
    private final StatClient statClient;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) Sorts sort,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {
        log.info("Request on getting events with parameters:\ntext={}\ncategories={}\npaid={}\nrangeStart={}" +
                "\nrangeEnd={}\nonlyAvailable={}\nsort={}\nfrom={}\nsize={}\nhas been received",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new NotValidRequestParametersException("rangeStart should be before than rangeEnd");
        }
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }
        List<EventShortDto> result = toEventShortDtos(eventService.getEvents(new GetEventsRequestParameters(
                null, List.of(State.PUBLISHED), categories, rangeStart, rangeEnd, from, size, text, paid, onlyAvailable, sort
        )));
        sendEndpointHit(request);
        return result;
    }

    @GetMapping("/{id}")
    public EventDto getPublishedEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Request on getting published event by id={} has been received", id);
        EventDto result = toEventDto(eventService.getPublishedEventById(id));
        sendEndpointHit(request);
        return result;
    }

    private void sendEndpointHit(HttpServletRequest request) {
        try {
            statClient.saveEndpointHit(new EndpointHitDto(null, "ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        } catch (CouldNotSaveHitException exception) {
            log.warn("Could not receive successful response from StatServer during trying to save endpoint hit\n{}", exception.getMessage());
        }
    }
}