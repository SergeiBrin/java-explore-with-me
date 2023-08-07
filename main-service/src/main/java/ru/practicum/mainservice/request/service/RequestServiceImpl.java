package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.model.ConflictException;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.request.enums.RequestStatus;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.model.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> findUserRequestsInEvents(Long userId) {
        userService.findUserById(userId);
        List<Request> findRequests = requestRepository.findByRequesterId(userId);
        log.info("GET запрос в PrivateRequestController обработан успешно. " +
                "Метод findUserRequestsInEvents(), findRequests={}", findRequests);

        return RequestMapper.buildParticipationRequestDtoList(findRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> findEventRequestsForUser(Long userId, Long eventId) {
        List<Request> findRequests = requestRepository.findByInitiatorIdAndEventId(userId, eventId);
        log.info("GET запрос в PrivateRequestController обработан успешно. " +
                "Метод findEventRequestsForUser(), findRequests={}", findRequests);

        return RequestMapper.buildParticipationRequestDtoList(findRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public Long findByEventIdAndStatus(Long eventId, RequestStatus status) {
        Long countRequestsForStatus = requestRepository.countByEventIdAndStatus(eventId, status);
        log.info("GET запрос из EventMapper обработан успешно. " +
                "Метод findByEventIdAndStatus(), countRequestsForStatus={}", countRequestsForStatus);

        return countRequestsForStatus;
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User findUser = userService.findUserById(userId);
        Event findEvent = findEventById(eventId);

        // Зона проверок по условиям спецификации
        checkThatEventIsNotOwnedByUser(eventId, userId);
        checkThatEventIsPublished(eventId);
        checkParticipationRequestLimit(findEvent, eventId);

        Request buildRequest = RequestMapper.buildRequest(findUser, findEvent);
        Request createRequest = requestRepository.save(buildRequest);
        log.info("POST запрос в PrivateRequestController обработан успешно. " +
                "Метод createRequest(), createRequest={}", createRequest);

        return RequestMapper.buildParticipationRequestDto(createRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        // Проверка, что запрос существует
        Request findRequest = findInitiatorRequestById(requestId, userId);
        findRequest.setStatus(RequestStatus.CANCELED);
        Request updateRequest = requestRepository.save(findRequest);
        log.info("PATCH запрос в PrivateRequestController обработан успешно. " +
                "Метод cancelRequest(), updateRequest={}", updateRequest);

        return RequestMapper.buildParticipationRequestDto(updateRequest);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatuses(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
    // 1. если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        // находим событие по id и id его создателя
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        RequestStatus updateStatus = updateRequest.getStatus();

        // Event findEventById = findEventById(eventId);
        // Проверяем, что Event принадлежит пользователю
        Event findEvent = findEventByIdAndInitiatorId(userId, eventId);

        List<Long> requestIds = updateRequest.getRequestIds();
        List<Request> findRequests = requestRepository.findByIdIn(requestIds);

        // Если лимит 0 или модерация false, то Events уже CONFIRMED
        if (findEvent.getParticipantLimit() == 0 || !findEvent.getRequestModeration()) {
            EventRequestStatusUpdateResult buildResult =
                    RequestMapper.buildEventRequestStatusUpdateResult(findRequests, new ArrayList<>());// подтверждение не требуется
            log.info("PATCH запрос в PrivateEventController обработан успешно. " +
                    "Метод updateRequestStatuses(), buildResult={}", buildResult);

            return buildResult;
        }

    // 2. нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        Long findConfirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Integer participantLimit = findEvent.getParticipantLimit();
        if (findConfirmedRequests == participantLimit.longValue()) {
            throw new ConflictException("The participant limit has been reached");
        }

    // 3. статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        Long findPendingRequests = requestRepository.countByIdInAndStatus(requestIds, RequestStatus.PENDING);
        // Если findPendingRequests меньше, чем ids.size(), значит ids - не все PENDING
        if (findPendingRequests < requestIds.size()) {
            throw new ConflictException("status can only be changed for applications that are in the pending state");
        }

        // Если статус на обновление REJECTED, то все отклоняем и возвращаем.
        if (updateStatus.equals(RequestStatus.REJECTED)) {
            rejectedRequests = RequestMapper.buildRequestListWithNewStatus(findRequests, RequestStatus.REJECTED);
            List<Request> updateRequestsOnRejected = requestRepository.saveAll(rejectedRequests);
            EventRequestStatusUpdateResult buildResultRejected = RequestMapper.buildEventRequestStatusUpdateResult(confirmedRequests, updateRequestsOnRejected);
            log.info("PATCH запрос в PrivateEventController обработан успешно. " +
                    "Метод updateRequestStatuses(), buildResultRejected={}", buildResultRejected);

            return buildResultRejected;
        }

        // 4. если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        // Вычисляем количество оставшихся заявок. Если не меньше, чем ids, то все одобряем
        int remainingConfirmations = (int) (participantLimit - findConfirmedRequests);
        if (remainingConfirmations >= requestIds.size()) {
            confirmedRequests = RequestMapper.buildRequestListWithNewStatus(findRequests, RequestStatus.CONFIRMED);
            List<Request> updateRequestsOnConfirmed = requestRepository.saveAll(confirmedRequests);
            EventRequestStatusUpdateResult buildResultConfirmed = RequestMapper.buildEventRequestStatusUpdateResult(updateRequestsOnConfirmed, rejectedRequests);
            log.info("PATCH запрос в PrivateEventController обработан успешно. " +
                    "Метод updateRequestStatuses(), buildResultConfirmed={}", buildResultConfirmed);

            return buildResultConfirmed;
        }

        // Если их меньше, то часть одобряем, часть отклоняем.
        confirmedRequests = RequestMapper.buildRequestListWithNewStatus(new ArrayList<>(
                findRequests.subList(0, remainingConfirmations)), RequestStatus.CONFIRMED);

        rejectedRequests = RequestMapper.buildRequestListWithNewStatus(new ArrayList<>(
                findRequests.subList(remainingConfirmations, findRequests.size())), RequestStatus.REJECTED);

        List<Request> updateRequestsOnConfirmed = requestRepository.saveAll(confirmedRequests);
        List<Request> updateRequestsOnRejected = requestRepository.saveAll(rejectedRequests);

        EventRequestStatusUpdateResult buildResult = RequestMapper.buildEventRequestStatusUpdateResult(updateRequestsOnConfirmed, updateRequestsOnRejected);
        log.info("PATCH запрос в PrivateEventController обработан успешно. " +
                "Метод updateRequestStatuses(), buildResult={}", buildResult);

        return buildResult;
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId) // тут бы вынести куда-то проверку
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Event findEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("The user with id=%d does not own the event with id=%d", userId, eventId)));
    }

    private Request findInitiatorRequestById(Long requestId, Long userId) {
        return requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));
    }

    // проверка на то, что создатель запроса не является инициатором этого события
    private void checkThatEventIsNotOwnedByUser(Long eventId, Long userId) {
        Optional<Event> event = eventRepository.findByIdAndInitiatorId(eventId, userId);

        // Здесь нужен код 409
        if (event.isPresent()) {
            throw new ConflictException(
                    String.format(
                    "Event initiator with id=%d cannot add participation request for Event with id=%d", userId, eventId));
        }
    }

    // проверка на то, что событие опубликовано
    private void checkThatEventIsPublished(Long eventId) {
        // здесь нужен код 409
        eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new ConflictException(String.format("Event with id=%d must be published", eventId)));
    }

    // Проверка, что количество Confirmed запросов на участие в событии не превысило его лимит
    private void checkParticipationRequestLimit(Event event, Long eventId) {
        Integer participantLimit = event.getParticipantLimit();
        // Если лимит 0 - значит лимита нет. Тогда return.
        if (participantLimit == 0) {
            return;
        }

        Long countOfEventRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (countOfEventRequests >= participantLimit) {
            throw new ConflictException(String.format(
                    "Count of requests for an event=%d exceeded the limit=%d set by an initiator",
                    countOfEventRequests, participantLimit));
        }
    }
}
