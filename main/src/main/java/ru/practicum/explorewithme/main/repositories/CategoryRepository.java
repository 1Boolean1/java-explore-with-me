package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.dtos.CategoryDto;
import ru.practicum.explorewithme.main.models.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT new ru.practicum.explorewithme.main.dtos.CategoryDto(h.id, h.name) " +
            "FROM Category h " +
            "ORDER BY h.id ")
    List<CategoryDto> findCategoriesWithFromAndSize(Pageable pageable);
}
