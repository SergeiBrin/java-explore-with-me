package ru.practicum.statservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statdto.dto.EndpointHitDto;
import ru.practicum.statdto.dto.ViewStatsDto;
import ru.practicum.statservice.mapper.EndpointHitMapper;
import ru.practicum.statservice.model.EndpointHit;
import ru.practicum.statservice.repository.StatisticsRepository;
import ru.practicum.statservice.utils.PageRequestFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatisticsRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return getStatisticsWithUniqueIp(start, end, uris);
        } else {
            return getStatisticsWithoutUniqueIp(start, end, uris);
        }
    }

    @Transactional
    @Override
    public void createHit(EndpointHitDto hitDto) {
        EndpointHit buildHit = EndpointHitMapper.buildEndpointHit(hitDto);
        repository.save(buildHit);
        log.info("");
    }

    @Transactional(readOnly = true)
    private List<ViewStatsDto> getStatisticsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<ViewStatsDto> stats;
        Pageable page = PageRequestFactory.createPageRequest(0, 10);

        if (!uris.isEmpty()) {
            stats = repository.getUniqueStatisticsWithCheckingLinks(start, end, uris, page);
            log.info("");
            return stats;
        }

        stats = repository.getUniqueStatisticsWithoutCheckingLinks(start, end, page);
        log.info("");
        return stats;
    }

    @Transactional(readOnly = true)
    private List<ViewStatsDto> getStatisticsWithoutUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<ViewStatsDto> stats;
        Pageable page = PageRequestFactory.createPageRequest(0, 10);

        if (!uris.isEmpty()) {
            stats = repository.getStatisticsWithCheckingLinks(start, end, uris, page);
            log.info("");
            return stats;
        }

        stats = repository.getStatisticsWithoutCheckingLinks(start, end, page);
        log.info("");
        return stats;
    }
}
