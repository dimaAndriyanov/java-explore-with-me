package ru.practicum.ewm.repository;

import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository {
    ViewStatsDto saveHit(EndpointHit hit);

    List<ViewStatsDto> getStats(List<LocalDateTime> period, List<String> uris, boolean unique, String app);
}