package ru.practicum.ewm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class ViewStatsDto {
    private final String app;
    private final String uri;
    private final long hits;
}