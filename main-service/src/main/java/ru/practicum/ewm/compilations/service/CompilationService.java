package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation createCompilation(Compilation compilation);

    void deleteCompilationById(Long id);

    Compilation getCompilationById(Long id);

    List<Compilation> getCompilations(Boolean pinned, int from, int size);

    Compilation updateCompilationById(Long id, Compilation compilation);
}