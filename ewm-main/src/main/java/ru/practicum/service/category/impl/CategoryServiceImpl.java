package ru.practicum.service.category.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exeption.ConstraintViolationException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.mapper.category.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.service.category.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(CategoryMapper.toModel(categoryDto));
        log.info("Category with id: {} added to DB", category.getId());
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        getCategoryIfExistOrThrow(catId);
        if (!eventRepository.findEventsByCategoryId(catId).isEmpty()) {
            throw new ConstraintViolationException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
        log.info("Category with catId: {} deleted from DB", catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = getCategoryIfExistOrThrow(catId);
        category.setName(categoryDto.getName());
        category = categoryRepository.save(category);
        log.info("Category with id: {} updated in DB", catId);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toDto(getCategoryIfExistOrThrow(catId));
    }

    private Category getCategoryIfExistOrThrow(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория с id " + catId +
                " не найдена в системе"));
    }
}
