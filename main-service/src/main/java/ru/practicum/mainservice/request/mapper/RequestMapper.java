package ru.practicum.mainservice.request.mapper;

import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.request.enums.RequestStatus;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.model.dto.ParticipationRequestDto;
import ru.practicum.mainservice.user.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static Request buildRequest(User user, Event event) {
        // Если инициатор события не установил пре-модерацию на участие в событии,
        // то запрос автоматом станет CONFIRMED, иначе PENDING
        RequestStatus status = RequestStatus.PENDING;
        Boolean isRequestModeration = event.getRequestModeration();

        // При подтверждении запросов, если лимит 0, то подтверждать не надо.
        // Вероятно он сразу становится CONFIRMED
        Integer participantLimit = event.getParticipantLimit();
        if (participantLimit == 0 || !isRequestModeration) {
            status = RequestStatus.CONFIRMED;
        }

        return Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(status)
                .build();
    }

    public static ParticipationRequestDto buildParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static EventRequestStatusUpdateResult buildEventRequestStatusUpdateResult(List<Request> confirmed, List<Request> rejected) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        confirmedRequests = confirmed
                .stream()
                .map(RequestMapper::buildParticipationRequestDto)
                .collect(Collectors.toList());

        rejectedRequests = rejected
                .stream()
                .map(RequestMapper::buildParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    public static List<Request> buildRequestListWithNewStatus(List<Request> requests, RequestStatus status) {
        return requests
                .stream()
                .peek(request -> request.setStatus(status))
                .collect(Collectors.toList());
    }

    public static List<ParticipationRequestDto> buildParticipationRequestDtoList(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::buildParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
