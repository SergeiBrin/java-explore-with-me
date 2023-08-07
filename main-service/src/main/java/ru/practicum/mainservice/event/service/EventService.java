package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.model.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> findEventsByUserId(Long userId, int from, int size);

    List<EventFullDto> findEventsByParameters(
            List<Long> users,
            List<State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size);

    List<EventShortDto> findPublishedEventsByParameters(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto findEventById(Long eventId);

    EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
