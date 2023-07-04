package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(hits.app, hits.uri, COUNT(hits.uri)) " +
            "FROM EndpointHit AS hits " +
            "WHERE hits.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY hits.app, hits.uri " +
            "ORDER BY COUNT(hits.uri) DESC")
    List<ViewStatsDto> getHits(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(hits.app, hits.uri, COUNT(DISTINCT hits.ip)) " +
            "FROM EndpointHit AS hits " +
            "WHERE hits.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY hits.app, hits.uri " +
            "ORDER BY COUNT(hits.ip) DESC")
    List<ViewStatsDto> getHitsWithUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(hits.app, hits.uri, COUNT(hits.uri)) " +
            "FROM EndpointHit AS hits " +
            "WHERE hits.timestamp BETWEEN ?1 AND ?2 " +
            "AND hits.uri IN ?3 " +
            "GROUP BY hits.app, hits.uri " +
            "ORDER BY COUNT(hits.uri) DESC")
    List<ViewStatsDto> getHitsWithUriList(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(hits.app, hits.uri, COUNT(DISTINCT hits.uri)) " +
            "FROM EndpointHit AS hits " +
            "WHERE hits.timestamp BETWEEN ?1 AND ?2 " +
            "AND hits.uri IN ?3 " +
            "GROUP BY hits.app, hits.uri " +
            "ORDER BY COUNT(hits.uri) DESC")
    List<ViewStatsDto> getHitsWithUriListUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}