package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    @Override
    public void createHit(EndpointHit hit) {
        long hitId = repository.saveHit(hit);
        log.info("New hit with id = {} has been saved", hitId);
    }

    @Override
    public List<ViewStatsDto> getStats(List<LocalDateTime> period, List<String> uris, boolean unique, String app) {
        return repository.getStats(period, uris, unique, app);
    }
}