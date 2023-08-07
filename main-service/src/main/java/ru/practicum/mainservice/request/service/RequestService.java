package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.request.enums.RequestStatus;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.model.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findUserRequestsInEvents(Long userid);

    List<ParticipationRequestDto> findEventRequestsForUser(Long userId, Long eventId);

    Long findByEventIdAndStatus(Long eventId, RequestStatus status);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateRequestStatuses(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);
}
