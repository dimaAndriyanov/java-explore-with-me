package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Location {
    @NotNull
    @Min(-90)
    @Max(90)
    private final double lat;

    @NotNull
    @Min(-180)
    @Max(180)
    private final double lon;
}