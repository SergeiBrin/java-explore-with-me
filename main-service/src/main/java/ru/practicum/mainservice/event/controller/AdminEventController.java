package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.model.dto.EventFullDto;
import ru.practicum.mainservice.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    // Поиск событий
    @GetMapping
    public ResponseEntity<List<EventFullDto>> findEventsByParameters(@RequestParam(required = false) List<Long> users,
                                                                     @RequestParam(required = false) List<State> states, // тут нужно разобраться, какие States, ведь их много всяких
                                                                     @RequestParam(required = false) List<Long> categories,
                                                                     @RequestParam(required = false)
                                                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                         LocalDateTime rangeStart,
                                                                     @RequestParam(required = false)
                                                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                         LocalDateTime rangeEnd,
                                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                     @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил GET запрос в AdminEventController. Метод findEventsByParameters(), " +
                "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}",
                users,states, categories, rangeStart, rangeEnd);
        List<EventFullDto> findEvents =
                eventService.findEventsByParameters(users, states, categories, rangeStart, rangeEnd, from, size);

        return new ResponseEntity<>(findEvents, HttpStatus.OK);
    }

    // Редактирование данных события и его статуса (отклонение/публикация).
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventAdminRequest(@PathVariable Long eventId,
                                                                @Valid @RequestBody
                                                                UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Поступил PATCH запрос в AdminEventController. " +
                        "Метод updateEventAdminRequest(), eventId={}, updateEventAdminRequest={}", eventId, updateEventAdminRequest);
        EventFullDto updateEvent = eventService.updateEventByAdmin(eventId, updateEventAdminRequest);

        return new ResponseEntity<>(updateEvent, HttpStatus.OK);
    }
}
