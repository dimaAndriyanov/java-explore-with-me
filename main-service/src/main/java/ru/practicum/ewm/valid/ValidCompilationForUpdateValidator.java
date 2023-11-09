package ru.practicum.ewm.valid;

import ru.practicum.ewm.compilations.dto.CompilationRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCompilationForUpdateValidator implements ConstraintValidator<ValidCompilationForUpdate, CompilationRequestDto> {
    @Override
    public boolean isValid(CompilationRequestDto compilation, ConstraintValidatorContext context) {
        if (compilation != null) {
            return (compilation.getTitle() != null && !compilation.getTitle().isBlank()) ||
                    (compilation.getPinned() != null) ||
                    (compilation.getEvents() != null);
        }
        return true;
    }
}