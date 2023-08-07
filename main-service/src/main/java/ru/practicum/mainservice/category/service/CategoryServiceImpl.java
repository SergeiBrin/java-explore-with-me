package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.dto.CategoryDto;
import ru.practicum.mainservice.category.model.dto.NewCategoryDto;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.utils.PageRequestFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Category> findAllCategories(int from, int size) {
        Pageable page = PageRequestFactory.buildPageRequestWithSort(from, size, Sort.by("id"));
        List<Category> findCategories = categoryRepository.findAll(page).getContent();
        log.info("");

        return findCategories;
    }

    @Transactional(readOnly = true)
    @Override
    public Category findCategoryById(Long catId) {
        Category findCategory = findCategory(catId);
        log.info("GET запрос в PublicCategoryController обработан успешно. " +
                "Метод findCategoryById(), findCategory={}", findCategory);

        return findCategory;
    }

    @Transactional
    @Override
    public Category createCategory(NewCategoryDto newCategoryDto) {
        Category buildCategory = CategoryMapper.buildCategoryForPost(newCategoryDto);
        Category createCategory = categoryRepository.save(buildCategory);
        log.info("POST запрос в AdminCategoryController обработан успешно. " +
                "Метод createCategory(), createCategory={}", createCategory);

        return createCategory;
    }

    @Transactional
    @Override
    public Category updateCategory(Long catId, CategoryDto categoryDto) {
        findCategory(catId);

        Category buildCategory = CategoryMapper.buildCategoryForPatch(catId, categoryDto);
        Category updateCategory = categoryRepository.save(buildCategory);
        log.info("PATCH запрос в AdminCategoryController обработан успешно. " +
                "Метод updateCategory(), updateCategory={}", updateCategory);

        return updateCategory;
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));

        categoryRepository.deleteById(catId);
        log.info("DELETE запрос в AdminCategoryController обработан успешно. Метод deleteCategoryById()");
    }

    private Category findCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
    }
}
