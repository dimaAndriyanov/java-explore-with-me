package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.valid.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
@Validated
public class EventRequestDto {
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 120)
    private final String title;

    @NotBlank(groups = OnCreate.class)
    @Size(min = 20, max = 2000)
    private final String annotation;

    @NotBlank(groups = OnCreate.class)
    @Size(min = 20, max = 7000)
    private final String description;

    @NotNull(groups = OnCreate.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @AtLeastTwoHoursAfterNow
    private final LocalDateTime eventDate;

    @NotNull(groups = OnCreate.class)
    @Valid
    private final Location location;

    private final Boolean paid;

    @PositiveOrZero
    private final Integer participantLimit;

    private final Boolean requestModeration;

    @NotNull(groups = OnCreate.class)
    private final Long category;

    @Null(groups = OnCreate.class)
    @ValidStateActionForUpdateByAdmin(groups = OnUpdateByAdmin.class)
    @ValidStateActionForUpdateByUser(groups = OnUpdateByUser.class)
    private final StateAction stateAction;
}