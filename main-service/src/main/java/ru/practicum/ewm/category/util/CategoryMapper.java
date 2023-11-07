package ru.practicum.ewm.category.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(CategoryRequestDto categoryRequestDto) {
        return new Category(null, categoryRequestDto.getName());
    }

    public Category toCategory(Long id, CategoryRequestDto categoryRequestDto) {
        return new Category(id, categoryRequestDto.getName());
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public List<CategoryDto> toCategoryDtos(List<Category> categories) {
        return categories
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}