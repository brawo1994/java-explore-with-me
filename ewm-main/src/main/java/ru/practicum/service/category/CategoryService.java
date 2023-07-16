package ru.practicum.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long catId);
}
