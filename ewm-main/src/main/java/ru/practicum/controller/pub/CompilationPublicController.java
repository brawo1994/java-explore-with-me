package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.util.Pagination;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCategories(@RequestParam(required = false) Boolean pinned,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request /compilations with params: pinned {}, from {}, size {}", pinned, from, size);
        return compilationService.getCompilations(pinned, new Pagination(from, size, Sort.by(ASC, "id")));
    }

    @GetMapping("/{compId}")
    public CompilationDto getCategory(@PathVariable("compId") Long compId) {
        log.info("Received GET request /compilations/{}", compId);
        return compilationService.getCompilationById(compId);
    }
}
