package ru.practicum.ewm.event.participation.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.participation.request.model.Status;
import ru.practicum.ewm.valid.ValidStatusForUpdate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor(force = true)
public class EventParticipationRequestStatusUpdateRequest {
    @NotNull
    @NotEmpty
    private final Set<Long> requestIds;

    @NotNull
    @ValidStatusForUpdate
    private final Status status;
}