package ru.practicum.mapper.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.mapper.event.EventMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;

import java.util.List;
import java.util.stream.Collectors;

public final class CompilationMapper {

    private CompilationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Compilation toModel(NewCompilationDto newCompilationDto, List<Event> eventList) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(eventList)
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
