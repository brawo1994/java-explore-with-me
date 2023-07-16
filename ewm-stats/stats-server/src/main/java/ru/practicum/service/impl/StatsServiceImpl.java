package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exeption.BadRequestException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(EndpointHitMapper.toModel(endpointHitDto));
        log.info("Hit with id: {} added to DB", endpointHit.getId());
        return EndpointHitMapper.toDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Дата и время начала диапазона не может быть меньше Даты и времени "
                    + "конца диапазона");

        }
        if (uris == null) {
            if (Boolean.TRUE.equals(unique)) {
                return statsRepository.getHitsWithUnique(start, end);
            } else {
                return statsRepository.getHits(start, end);
            }
        } else {
            if (Boolean.TRUE.equals(unique)) {
                return statsRepository.getHitsWithUriListUnique(start, end, uris);
            } else {
                return statsRepository.getHitsWithUriList(start, end, uris);
            }
        }
    }
}
