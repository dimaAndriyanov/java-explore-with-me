package ru.practicum.ewm.category.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(force = true)
public class CategoryRequestDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private final String name;
}