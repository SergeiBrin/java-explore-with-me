package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.dto.CategoryDto;
import ru.practicum.mainservice.category.model.dto.NewCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Поступил POST запрос в AdminCategoryController: " +
                "метод createCategory(), newCategoryDto={}", newCategoryDto);
        Category createCategory = categoryService.createCategory(newCategoryDto);

        return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long catId,
                                                   @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Поступил PATCH запрос в AdminCategoryController: " +
                "метод updateCategory(), catId={}, categoryDto={}", catId, categoryDto);
        Category updateCategory = categoryService.updateCategory(catId, categoryDto);

        return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long catId) {
        log.info("Поступил DELETE запрос в AdminCategoryController: " +
                "метод deleteCategoryById(), catId={}", catId);
        categoryService.deleteCategoryById(catId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
