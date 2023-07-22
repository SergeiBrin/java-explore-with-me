package ru.practicum.statservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statdto.dto.EndpointHitDto;
import ru.practicum.statdto.dto.ViewStatsDto;
import ru.practicum.statservice.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService service;

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @NotNull
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime start,
                                                       @RequestParam @NotNull
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime end,
                                                       @RequestParam(defaultValue = "") List<String> uris,
                                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("");

        List<ViewStatsDto> viewStats = service.getStats(start, end, uris, unique);
        return new ResponseEntity<>(viewStats, HttpStatus.OK);
    }

    // В спецификации просят вернуть только код ответа.
    // Это же подтвердил наставник. Поэтому сделал так.
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("");
        service.createHit(hitDto);
    }
}
