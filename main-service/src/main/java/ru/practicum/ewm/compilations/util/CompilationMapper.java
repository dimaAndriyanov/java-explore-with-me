package ru.practicum.ewm.compilations.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationRequestDto;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.util.EventMapper.*;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                toEventShortDtos(compilation.getEvents())
        );
    }

    public List<CompilationDto> toCompilationDtos(List<Compilation> compilations) {
        return compilations
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    public Compilation toCompilation(CompilationRequestDto compilationRequest, List<Event> events, boolean isCreated) {
        Boolean pinned;
        if (isCreated && compilationRequest.getPinned() == null) {
            pinned = false;
        } else {
            pinned = compilationRequest.getPinned();
        }
        return new Compilation(
                null,
                compilationRequest.getTitle(),
                pinned,
                events
        );
    }
}