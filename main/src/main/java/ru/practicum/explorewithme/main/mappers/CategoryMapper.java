package ru.practicum.explorewithme.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.models.Category;

@UtilityClass
public class CategoryMapper {
    public static CategoryDto mapCategoryToCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
