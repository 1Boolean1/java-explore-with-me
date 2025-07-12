package ru.practicum.explorewithme.main.mappers;

import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.models.Category;

public class CategoryMapper {
    public static Category mapToCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName());
    }

    public static CategoryDto mapCategoryToCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
