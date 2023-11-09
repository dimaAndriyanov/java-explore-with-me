package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.compilations.util.CompilationMapper.*;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Request on getting compilations with parameters boolean={}, from={}, size={} has been received",
                pinned, from, size);
        return toCompilationDtos(compilationService.getCompilations(pinned, from, size));
    }

    @GetMapping("/{id}")
    public CompilationDto getCompilationById(@PathVariable Long id) {
        log.info("Request on getting compilation with id={} has been received", id);
        return toCompilationDto(compilationService.getCompilationById(id));
    }
}