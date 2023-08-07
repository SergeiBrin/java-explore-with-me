package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.request.model.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {
    private final RequestService requestService;

    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findUserRequestsInEvents(@PathVariable Long userId) {
        log.info("Поступил GET запрос в PrivateRequestController. Метод findUserRequestsInEvents(), userId={}", userId);
        List<ParticipationRequestDto> userRequestInEvents = requestService.findUserRequestsInEvents(userId);

        return new ResponseEntity<>(userRequestInEvents, HttpStatus.OK);
    }

    // Добавление запроса от текущего пользователя на участие в событии
    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam Long eventId) {
        log.info("Поступил POST запрос в PrivateRequestController. " +
                "Метод createRequest(), userId={}, eventId={}", userId, eventId);
        ParticipationRequestDto createRequest = requestService.createRequest(userId, eventId);

        return new ResponseEntity<>(createRequest, HttpStatus.CREATED);
    }

    // Отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        log.info("Поступил PATCH запрос в PrivateRequestController. " +
                "Метод cancelRequest(), userId={}, requestId={}", userId, requestId);
        ParticipationRequestDto cancelRequest = requestService.cancelRequest(userId, requestId);

        return new ResponseEntity<>(cancelRequest, HttpStatus.OK);
    }
}
