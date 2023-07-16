package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Received POST request /admin/categories with body: {}", categoryDto.toString());
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("catId") Long catId) {
        log.info("Received DELETE request /admin/categories/{}", catId);
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategoryById(@PathVariable("catId") Long catId,
                                          @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Received PATCH request /admin/categories/{} with body: {}", catId, categoryDto.toString());
        return categoryService.updateCategory(catId, categoryDto);
    }
}
