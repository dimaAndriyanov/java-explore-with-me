package ru.practicum.ewm.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(force = true)
public class UserRequestDto {
    @NotBlank
    @Size(min = 2, max = 250)
    private final String name;
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private final String email;
}