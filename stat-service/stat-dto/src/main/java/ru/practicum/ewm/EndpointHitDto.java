package ru.practicum.ewm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class EndpointHitDto {
    private final Long id;
    @NotBlank
    private final String app;
    @NotBlank
    private final String uri;
    @NotBlank
    private final String ip;
    @NotBlank
    private final String timestamp;
}