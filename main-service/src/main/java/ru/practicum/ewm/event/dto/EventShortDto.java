package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventShortDto {
    private final Long id;
    private final String title;
    private final String annotation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    private final boolean paid;
    private final UserShortDto initiator;
    private final CategoryDto category;
    private final int confirmedRequests;
    private final Long views;
}