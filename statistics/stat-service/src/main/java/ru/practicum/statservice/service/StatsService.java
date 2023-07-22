package ru.practicum.statservice.service;

import ru.practicum.statdto.dto.EndpointHitDto;
import ru.practicum.statdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique);

    void createHit(EndpointHitDto hitDto);
}
