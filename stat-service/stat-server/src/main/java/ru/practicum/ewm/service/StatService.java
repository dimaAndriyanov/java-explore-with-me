package ru.practicum.ewm.service;

import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void createHit(EndpointHit hit);

    List<ViewStatsDto> getStats(List<LocalDateTime> period, List<String> uris, boolean unique, String app);
}