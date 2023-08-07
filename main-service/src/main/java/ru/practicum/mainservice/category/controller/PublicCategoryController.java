package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> findAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил GET запрос в PublicCategoryController: метод findAllCategories()");
        List<Category> findCategories = categoryService.findAllCategories(from, size);

        return new ResponseEntity<>(findCategories, HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Category> findCategoryById(@PathVariable Long catId) {
        log.info("Поступил GET запрос в PublicCategoryController: метод findCategoryById(), catId={}", catId);
        Category findCategory = categoryService.findCategoryById(catId);

        return new ResponseEntity<>(findCategory, HttpStatus.OK);
    }
}
