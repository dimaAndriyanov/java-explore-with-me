package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventDto {
    private final Long id;
    private final String title;
    private final String annotation;
    private final String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    private final Location location;
    private final boolean paid;
    private final int participantLimit;
    private final boolean requestModeration;
    private final State state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime publishedOn;
    private final UserShortDto initiator;
    private final CategoryDto category;
    private final int confirmedRequests;
    private final Long views;
}