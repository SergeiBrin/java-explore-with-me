package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.model.dto.EventFullDto;
import ru.practicum.mainservice.event.model.dto.EventShortDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.statclient.client.HttpClient;
import ru.practicum.statdto.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final EventService eventService;
    private final HttpClient httpClient;

    // Получение событий с возможностью фильтрации
    @GetMapping
    public ResponseEntity<List<EventShortDto>> findEventsByParameters(@RequestParam(required = false) String text,
                                                                      @RequestParam(required = false) List<Long> categories,
                                                                      @RequestParam(required = false) Boolean paid,
                                                                      @RequestParam(required = false)
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                          LocalDateTime rangeStart,
                                                                      @RequestParam(required = false)
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                          LocalDateTime rangeEnd,
                                                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                      @RequestParam(required = false) String sort,
                                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                      @RequestParam(defaultValue = "10") @Positive int size,
                                                                      HttpServletRequest request) {
        log.info("Поступил GET запрос в PublicEventController. Метод findEventsByParameters(), " +
                "text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, request={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, request);

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        log.info("client ip: {}", ip);
        log.info("endpoint path: {}", path);

        // Сохранение в статистику
        httpClient.createHit(
                new EndpointHitDto("ewm-main-service", path, ip, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        List<EventShortDto> findEvents = eventService
                .findPublishedEventsByParameters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return new ResponseEntity<>(findEvents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> findEventById(@PathVariable Long id,
                                                      HttpServletRequest request) {
        log.info("Поступил GET запрос в PublicEventController. " +
                "Метод findEventById(), id={}, request={}", id, request);

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        log.info("client ip: {}", ip);
        log.info("endpoint path: {}", path);

        // Сохранение в статистику
        httpClient.createHit(
                new EndpointHitDto("ewm-main-service", path, ip, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        EventFullDto findEventById = eventService.findEventById(id);

        return new ResponseEntity<>(findEventById, HttpStatus.OK);
    }
}
