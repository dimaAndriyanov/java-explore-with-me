package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public Compilation createCompilation(Compilation compilation) {
        compilationRepository.save(compilation);
        log.info("Compilation\n{}\n has been created", compilation);
        return compilation;
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long id) {
        if (compilationRepository.findById(id).isEmpty()) {
            throw new ObjectNotFoundException("Compilation with id=" + id + " was not found");
        }
        compilationRepository.deleteById(id);
        log.info("Compilation with id={} has been deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Compilation getCompilationById(Long id) {
        try {
            return compilationRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Compilation with id=" + id + " was not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Compilation> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, pageRequest).getContent();
    }

    @Override
    @Transactional
    public Compilation updateCompilationById(Long id, Compilation compilation) {
        Compilation savedCompilation;
        try {
            savedCompilation = compilationRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Compilation with id=" + id + " was not found");
        }
        compilationRepository.saveAndFlush(new Compilation(
                id,
                compilation.getTitle() == null ? savedCompilation.getTitle() : compilation.getTitle(),
                compilation.getPinned() == null ? savedCompilation.getPinned() : compilation.getPinned(),
                compilation.getEvents() == null ? savedCompilation.getEvents() : compilation.getEvents()
        ));
        log.info("Compilation\n{}\nhas been updated", savedCompilation);
        return savedCompilation;
    }
}