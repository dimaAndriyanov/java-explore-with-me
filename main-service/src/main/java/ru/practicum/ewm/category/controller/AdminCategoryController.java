package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

import static ru.practicum.ewm.category.util.CategoryMapper.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info("Request on creating Category with name={} has been received", categoryRequestDto.getName());
        return toCategoryDto(categoryService.createCategory(toCategory(categoryRequestDto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.info("Request on deleting Category with id={} has been received", id);
        categoryService.deleteCategoryById(id);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto,
                                      @PathVariable Long id) {
        log.info("Request on updating Category with id={} and name={} has been received", id, categoryRequestDto.getName());
        return toCategoryDto(categoryService.updateCategory(toCategory(id, categoryRequestDto)));
    }
}