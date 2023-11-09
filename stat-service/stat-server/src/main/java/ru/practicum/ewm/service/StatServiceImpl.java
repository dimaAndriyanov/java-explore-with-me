package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    @Override
    public ViewStatsDto createHit(EndpointHit hit) {
        return repository.saveHit(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(List<LocalDateTime> period, List<String> uris, boolean unique, String app) {
        return repository.getStats(period, uris, unique, app);
    }
}