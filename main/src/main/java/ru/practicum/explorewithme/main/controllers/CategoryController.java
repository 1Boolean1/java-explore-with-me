package ru.practicum.explorewithme.main.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dtos.CategoryCreateDto;
import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.services.CategoryService;

import java.util.List;

@RestController
@Slf4j
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid final CategoryCreateDto categoryDto) {
        log.info("Create category: {}", categoryDto);
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/admin/categories/{categoryId}")
    public CategoryDto updateCategory(@PathVariable @Positive Long categoryId, @RequestBody @Valid final CategoryDto categoryDto) {
        log.info("Update category: {}", categoryDto);
        return categoryService.patchCategory(categoryId, categoryDto);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive final Long id) {
        log.info("Delete category: {}", id);
        categoryService.deleteCategory(id);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Get categories from {}, size {}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategory(@PathVariable @Positive final Long id) {
        log.info("Get category: {}", id);
        return categoryService.getCategoryById(id);
    }
}
