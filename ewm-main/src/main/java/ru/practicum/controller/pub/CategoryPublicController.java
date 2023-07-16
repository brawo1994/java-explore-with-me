package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;
import ru.practicum.util.Pagination;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request /categories with params: from {}, size {}", from, size);
        return categoryService.getCategories(new Pagination(from, size, Sort.by(ASC, "id")));
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable("catId") Long catId) {
        log.info("Received GET request /categories/{}", catId);
        return categoryService.getCategoryById(catId);
    }
}
