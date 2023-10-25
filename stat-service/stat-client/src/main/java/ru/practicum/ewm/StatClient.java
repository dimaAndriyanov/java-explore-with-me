package ru.practicum.ewm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatClient {
    private final RestTemplate rest;
    private final ObjectMapper objectMapper;
    private final String appName;

    private static final String HEADER_USER_APP = "X-Stat-Server-User-App";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatClient(@Value("${stat-server.url}") String serverUrl,
                      @Value("${stat-server.user-app-name}") String appName,
                      RestTemplateBuilder builder, ObjectMapper objectMapper) {
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        this.objectMapper = objectMapper;
        this.appName = appName;
    }

    public void saveEndpointHit(EndpointHitDto hit) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hit, defaultHeaders());

        ResponseEntity<Object> statServerResponse;

        try {
            statServerResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException exception) {
            throw new CouldNotSaveHitException(exception.getResponseBodyAsString());
        }

        if (!statServerResponse.getStatusCode().is2xxSuccessful()) {
            String message = statServerResponse.getBody() == null ? "Internal error has occurred on stat server" :
                    statServerResponse.getBody().toString();
            throw new CouldNotSaveHitException(message);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<String> statServerResponse;

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .newInstance()
                .path("/stats")
                .queryParam("start", start.format(DATE_TIME_FORMATTER))
                .queryParam("end", end.format(DATE_TIME_FORMATTER));
        if (unique != null) {
            uriComponentsBuilder.queryParam("unique", unique);
        }
        if (uris != null) {
            for (String uri : uris) {
                uriComponentsBuilder.queryParam("uris", uri);
            }
        }

        try {
            statServerResponse = rest.exchange(uriComponentsBuilder.build().toUriString(),
                    HttpMethod.GET, requestEntity, String.class);
        } catch (HttpStatusCodeException exception) {
            throw new CouldNotGetStatsException(exception.getResponseBodyAsString());
        }
        if (!statServerResponse.getStatusCode().is2xxSuccessful()) {
            String message = statServerResponse.getBody() == null ? "Internal error has occurred on stat server" :
                    statServerResponse.getBody();
            throw new CouldNotGetStatsException(message);
        }
        try {
            return objectMapper.readValue(statServerResponse.getBody(), new TypeReference<>() {});
        } catch (Throwable exception) {
            throw new CouldNotGetStatsException("Could not read response from stat server");
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(HEADER_USER_APP, appName);
        return headers;
    }
}