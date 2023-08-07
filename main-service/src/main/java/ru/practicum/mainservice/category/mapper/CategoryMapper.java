package ru.practicum.mainservice.category.mapper;

import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.dto.CategoryDto;
import ru.practicum.mainservice.category.model.dto.NewCategoryDto;

public class CategoryMapper {

    public static Category buildCategoryForPost(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static Category buildCategoryForPatch(Long catId, CategoryDto categoryDto) {
        return Category.builder()
                .id(catId)
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto buildCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
