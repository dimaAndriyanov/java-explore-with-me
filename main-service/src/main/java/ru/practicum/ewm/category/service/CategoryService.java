package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    void deleteCategoryById(Long id);

    Category updateCategory(Category category);

    List<Category> getCategories(int from, int size);

    Category getCategoryById(Long id);
}