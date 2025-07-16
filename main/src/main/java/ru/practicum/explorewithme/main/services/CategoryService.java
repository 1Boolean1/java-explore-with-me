package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.CategoryCreateDto;
import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.ExistsException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.CategoryMapper;
import ru.practicum.explorewithme.main.models.Category;
import ru.practicum.explorewithme.main.repositories.CategoryRepository;
import ru.practicum.explorewithme.main.repositories.EventRepository;

import java.util.List;

@Service
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryService(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public CategoryDto addCategory(CategoryCreateDto categoryDto) {
        if (categoryDto.getName().isBlank()) {
            log.error("Category name is blank");
            throw new BadRequestException("Name cannot be blank");
        }
        if (categoryRepository.findAll().stream().anyMatch(c -> c.getName().equals(categoryDto.getName()))) {
            log.error("Category name already exists");
            throw new ExistsException("Category name already exists");
        }
        Category newCategory = new Category();
        newCategory.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(newCategory);

        return CategoryMapper.mapCategoryToCategoryDto(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        if (eventRepository.findAll().stream().anyMatch(event -> event.getCategory().getId().equals(categoryId))) {
            log.error("Event with this category already exist");
            throw new ExistsException("Event with this category already exist");
        }
        if (categoryRepository.findById(categoryId.intValue()).isEmpty()) {
            log.error("Category with id {} not found", categoryId);
            throw new NotFoundException("Category does not exist");
        }
        categoryRepository.deleteById(categoryId.intValue());
    }

    @Transactional
    public CategoryDto patchCategory(Long categoryId, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(categoryId.intValue())
                .orElseThrow(() -> new NotFoundException("Category does not exist"));

        boolean needsUpdate = false;

        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            if (categoryRepository.findAll().stream().anyMatch(c -> c.getName().equals(categoryDto.getName()))) {
                if (!existingCategory.getName().equals(categoryDto.getName())) {
                    log.error("Category name already exists");
                    throw new ExistsException("Category name already exists");
                }
            }
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
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepository.findCategoriesWithFromAndSize(pageRequest);
    }

    public CategoryDto getCategoryById(Long categoryId) {
        if (categoryRepository.findById(categoryId.intValue()).isEmpty()) {
            log.error("Category with id {} not found", categoryId);
            throw new NotFoundException("Category does not exist");
        }
        return CategoryMapper.mapCategoryToCategoryDto(categoryRepository.findById(categoryId.intValue()).get());
    }
}
