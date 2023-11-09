package ru.practicum.ewm.compilations.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.valid.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor(force = true)
public class CompilationRequestDto {
    @NotBlank(groups = OnCreate.class)
    @Size(min = 1, max = 50)
    private final String title;

    private final Boolean pinned;

    private final Set<Long> events;
}