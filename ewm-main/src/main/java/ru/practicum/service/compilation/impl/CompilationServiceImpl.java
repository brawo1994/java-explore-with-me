package ru.practicum.service.compilation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.mapper.compilation.CompilationMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;
import ru.practicum.repository.compilation.CompilationRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.service.compilation.CompilationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            for (Long eventId : newCompilationDto.getEvents()) {
                events.add(getEventIfExistOrThrow(eventId));
            }
        }
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.toModel(newCompilationDto, events));
        log.info("Compilation with id: {} added to DB", compilation.getId());
        return CompilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        getCompilationIfExistOrThrow(compId);
        compilationRepository.deleteById(compId);
        log.info("Compilation with id: {} deleted from DB", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = getCompilationIfExistOrThrow(compId);
        List<Event> events = new ArrayList<>();
        if (updateCompilationDto.getEvents() != null) {
            for (Long eventId : updateCompilationDto.getEvents()) {
                events.add(getEventIfExistOrThrow(eventId));
            }
        }
        compilation.setEvents(events);

        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Compilation with id: {} updated in DB", compId);
        return CompilationMapper.toDto(updatedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return CompilationMapper.toDto(getCompilationIfExistOrThrow(compId));
    }

    private Compilation getCompilationIfExistOrThrow(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка событий с id " +
                compId + " не найдена в системе"));
    }

    private Event getEventIfExistOrThrow(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId +
                " не найдено в системе"));
    }
}
