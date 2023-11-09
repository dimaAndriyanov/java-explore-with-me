package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationRequestDto;
import ru.practicum.ewm.compilations.service.CompilationService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.valid.OnCreate;
import ru.practicum.ewm.valid.OnUpdate;
import ru.practicum.ewm.valid.ValidCompilationForUpdate;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.ewm.compilations.util.CompilationMapper.*;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnCreate.class)
    public CompilationDto createCompilation(@Valid @RequestBody CompilationRequestDto compilationRequestDto) {
        log.info("Request on creating compilation with title={}, pinned={} and eventIds={} has been received",
                compilationRequestDto.getTitle(), compilationRequestDto.getPinned(), compilationRequestDto.getEvents());
        List<Event> events = getEvents(compilationRequestDto, true);
        return toCompilationDto(compilationService.createCompilation(toCompilation(compilationRequestDto, events, true)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long id) {
        log.info("Request on deleting compilation with id={} has been received", id);
        compilationService.deleteCompilationById(id);
    }

    @PatchMapping("/{id}")
    @Validated(OnUpdate.class)
    public CompilationDto updateCompilationById(@PathVariable Long id,
                                                @Valid
                                                @ValidCompilationForUpdate
                                                @RequestBody CompilationRequestDto compilationRequestDto) {
        log.info("Request on updating compilation with id={} and body={} has been received", id, compilationRequestDto);
        List<Event> events = getEvents(compilationRequestDto, false);
        return toCompilationDto(compilationService.updateCompilationById(id, toCompilation(compilationRequestDto, events, false)));
    }

    private List<Event> getEvents(CompilationRequestDto compilationRequestDto, boolean isCreated) {
        List<Event> events;
        if (compilationRequestDto.getEvents() != null && !compilationRequestDto.getEvents().isEmpty()) {
            events = eventService.getEventsById(compilationRequestDto.getEvents());
            if (events.size() < compilationRequestDto.getEvents().size()) {
                throw new ObjectNotFoundException("Some events with id from events=" +
                        compilationRequestDto.getEvents() + " was not found");
            }
        } else {
            if (isCreated) {
                events = List.of();
            } else {
                events = compilationRequestDto.getEvents() == null ? null : List.of();
            }
        }
        return events;
    }
}