package ru.practicum.explorewithme.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.gateway.client.StatsClient;
import ru.practicum.explorewithme.main.dtos.CategoryCreateDto;
import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.services.CategoryService;

import java.util.List;

@RestController
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final StatsClient statsClient;

    public CategoryController(final CategoryService categoryService, StatsClient statsClient) {
        this.categoryService = categoryService;
        this.statsClient = statsClient;
    }

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@RequestBody @Valid final CategoryCreateDto categoryDto, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Create category: {}", categoryDto);
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(@RequestBody @Valid final CategoryDto categoryDto, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Update category: {}", categoryDto);
        return categoryService.patchCategory(categoryDto);
    }

    @DeleteMapping("/admin/categories/{id}")
    public void deleteCategory(@PathVariable final Long id, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Delete category: {}", id);
        categoryService.deleteCategory(id);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Get categories from {}, size {}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategory(@PathVariable final Long id, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Get category: {}", id);
        return categoryService.getCategoryById(id);
    }
}
