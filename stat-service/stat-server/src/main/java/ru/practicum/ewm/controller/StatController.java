package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.service.StatService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.ewm.util.StatMapper.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService service;

    private static final String HEADER_USER_APP = "X-Stat-Server-User-App";

    @PostMapping("/hit")
    public void postHit(@Valid @RequestBody EndpointHitDto hit) {
        log.info("Request on posting endpoint hit with\napp = {}\nuri = {}\nip = {}\ntimestamp = {}\nhas been received",
                hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
        service.createHit(mapToEndpointHit(hit));
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique,
                                       @RequestHeader(name = HEADER_USER_APP, defaultValue = "ewm-main-service") String app) {
        StringBuilder urisSB;
        if (uris == null || uris.isEmpty()) {
            urisSB = new StringBuilder("[]");
        } else {
            urisSB = new StringBuilder("[\n");
            for (String uri : uris) {
                urisSB.append(uri).append("\n");
            }
            urisSB.append("]");
        }
        log.info("Request on getting statistics with parameters\nstart = {}\nend = {}\nuris = {}\nunique = {}\napp = {}" +
                "\nhas been received", start, end, urisSB, unique, app);
        return service.getStats(mapPeriod(start, end), uris, unique, app);
    }
}