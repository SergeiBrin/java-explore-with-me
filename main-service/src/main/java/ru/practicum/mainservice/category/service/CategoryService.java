package ru.practicum.mainservice.category.service;

import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.dto.CategoryDto;
import ru.practicum.mainservice.category.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories(int from, int size);

    Category findCategoryById(Long catId);

    Category createCategory(NewCategoryDto newCategoryDto);

    Category updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategoryById(Long catId);
}
