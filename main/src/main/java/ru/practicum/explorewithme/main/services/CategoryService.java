package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.CategoryCreateDto;
import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.CategoryMapper;
import ru.practicum.explorewithme.main.models.Category;
import ru.practicum.explorewithme.main.repositories.CategoryRepository;

import java.util.List;

@Service
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto addCategory(CategoryCreateDto categoryDto) {
        if (categoryDto.getName().isBlank()) {
            log.error("Category name is blank");
            throw new BadRequestException("Name cannot be blank");
        }
        Category newCategory = new Category();
        newCategory.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(newCategory);

        return CategoryMapper.mapCategoryToCategoryDto(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        if (categoryRepository.findById(categoryId.intValue()).isEmpty()) {
            log.error("Category with id {} not found", categoryId);
            throw new NotFoundException("Category does not exist");
        }
        categoryRepository.deleteById(categoryId.intValue());
    }

    @Transactional
    public CategoryDto patchCategory(CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(categoryDto.getId().intValue())
                .orElseThrow(() -> new NotFoundException("Category does not exist"));

        boolean needsUpdate = false;

        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            if (!existingCategory.getName().equals(categoryDto.getName())) {
                log.info("Updating category with name {}", categoryDto.getName());
                existingCategory.setName(categoryDto.getName());
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            Category updatedCategory = categoryRepository.save(existingCategory);
            return CategoryMapper.mapCategoryToCategoryDto(updatedCategory);
        } else {
            return CategoryMapper.mapCategoryToCategoryDto(existingCategory);
        }
    }

    public List<CategoryDto> getCategories(int from, int size) {
        if (from < categoryRepository.findAll().size()) {
            log.error("From must be less than or equal to category count");
            throw new BadRequestException("From must be less than or equal to category count");
        }

        return categoryRepository.findCategoriesWithFromAndSize(from, size);
    }

    public CategoryDto getCategoryById(Long categoryId) {
        if (categoryRepository.findById(categoryId.intValue()).isEmpty()) {
            log.error("Category with id {} not found", categoryId);
            throw new NotFoundException("Category does not exist");
        }
        return CategoryMapper.mapCategoryToCategoryDto(categoryRepository.findById(categoryId.intValue()).get());
    }
}
