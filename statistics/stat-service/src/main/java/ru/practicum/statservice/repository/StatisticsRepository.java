package ru.practicum.statservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.statdto.dto.ViewStatsDto;
import ru.practicum.statservice.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.statdto.dto.ViewStatsDto(eh.app, eh.uri, COUNT(eh.app)) " +
            "FROM EndpointHit eh " +
            "WHERE (eh.timestamp BETWEEN :start AND :end) " +
            "AND eh.uri IN :uris " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.app) DESC")
    List<ViewStatsDto> getStatisticsWithCheckingLinks(LocalDateTime start, LocalDateTime end, List<String> uris, Pageable page);

    @Query("SELECT new ru.practicum.statdto.dto.ViewStatsDto(eh.app, eh.uri, COUNT(eh.app)) " +
            "FROM EndpointHit eh " +
            "WHERE (eh.timestamp BETWEEN :start AND :end) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.app) DESC")
    List<ViewStatsDto> getStatisticsWithoutCheckingLinks(LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("SELECT new ru.practicum.statdto.dto.ViewStatsDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
           "FROM EndpointHit eh " +
           "WHERE (eh.timestamp BETWEEN :start AND :end) " +
           "AND eh.uri IN :uris " +
           "GROUP BY eh.app, eh.uri")
    List<ViewStatsDto> getUniqueStatisticsWithCheckingLinks(LocalDateTime start, LocalDateTime end, List<String> uris, Pageable page);

    @Query("SELECT new ru.practicum.statdto.dto.ViewStatsDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE (eh.timestamp BETWEEN :start AND :end) " +
            "GROUP BY eh.app, eh.uri")
    List<ViewStatsDto> getUniqueStatisticsWithoutCheckingLinks(LocalDateTime start, LocalDateTime end, Pageable page);

}