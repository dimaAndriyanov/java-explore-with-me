package ru.practicum.ewm.event.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.valid.ValidStateActionForUpdateByAdmin;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(force = true)
public class ModerationReplyDto {
    @NotNull
    private final Long eventId;

    @NotNull
    @ValidStateActionForUpdateByAdmin
    private final StateAction action;
}