package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.model.dto.EventFullDto;
import ru.practicum.mainservice.event.model.dto.EventShortDto;
import ru.practicum.mainservice.event.model.dto.NewEventDto;
import ru.practicum.mainservice.event.model.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.model.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    // Получение событий, добавленных текущим пользователем
    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> findEventsByUserId(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил GET запрос в PrivateEventController. Метод findEventsByUserId(), userId={}", userId);
        List<EventShortDto> findEvents = eventService.findEventsByUserId(userId, from, size);

        return new ResponseEntity<>(findEvents, HttpStatus.OK);
    }

    // Получение полной информации о событии добавленном текущим пользователем
    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> findEventById(@PathVariable Long userId,
                                                      @PathVariable Long eventId) {
        log.info("Поступил GET запрос в PrivateEventController. " +
                "Метод findEventById(), userId={}, eventId={}", userId, eventId);
        EventFullDto findEventById = eventService.findEventByUserIdAndEventId(userId, eventId);

        return new ResponseEntity<>(findEventById, HttpStatus.OK);
    }

    // Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findEventRequestsForUser(@PathVariable Long userId,
                                                                                  @PathVariable Long eventId) {
        log.info("Поступил GET запрос в PrivateEventController. " +
                "Метод findEventRequestsForUser(), userId={}, eventId={}", userId, eventId);
        List<ParticipationRequestDto> findRequests = requestService.findEventRequestsForUser(userId, eventId);

        return new ResponseEntity<>(findRequests, HttpStatus.OK);
    }

    // Добавление нового события
    // дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Поступил POST запрос в PrivateEventController. " +
                "Метод createEvent(), userId={}, newEventDto={}", userId, newEventDto);
        EventFullDto createEvent = eventService.createEvent(userId, newEventDto);

        return new ResponseEntity<>(createEvent, HttpStatus.CREATED);
    }

    // Изменение события добавленного текущим пользователем
    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("Поступил PATCH запрос в PrivateEventController. " +
                "Метод updateEventByUser(), userId={}, eventId={}, updateEvent={}", userId, eventId, updateEvent);
        EventFullDto updateEventByUser = eventService.updateEventByUser(userId, eventId, updateEvent);

        return new ResponseEntity<>(updateEventByUser, HttpStatus.OK);
    }

    // Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatuses(@PathVariable Long userId,
                                                                                @PathVariable Long eventId,
                                                                                @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Поступил PATCH запрос в PrivateEventController. " +
                "Метод updateRequestStatuses(), userId={}, eventId={}, updateRequest={}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResult updateRequestStatuses =
                requestService.updateRequestStatuses(userId, eventId, updateRequest);

        return new ResponseEntity<>(updateRequestStatuses, HttpStatus.OK);
    }
}
