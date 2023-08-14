package ru.practicum.mainservice.event.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.model.dto.CategoryDto;
import ru.practicum.mainservice.event.enums.AdminState;
import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.enums.UserState;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.dto.*;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.request.enums.RequestStatus;
import ru.practicum.mainservice.request.service.RequestService;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.model.dto.UserShortDto;
import ru.practicum.statclient.client.HttpClient;
import ru.practicum.statdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventMapper {
    private final RequestService requestService;
    private final HttpClient httpClient;

    public Event buildEventForPost(User initiator, Category category, Location location, NewEventDto newEventDto) {
        Event buildEvent = Event.builder()
                .initiator(initiator)
                .category(category)
                .location(location)
                .annotation(newEventDto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .state(State.PENDING) // При создании события ожидает подтверждения от Админа
                .title(newEventDto.getTitle())
                .build();

        return buildEvent;
    }

    public Event buildEventForUserPatch(Event eventForUpdate,
                                        UpdateEventUserRequest updateEventByUser,
                                        Category userCategory,
                                        Location userLocation) {

        String userAnnotation = updateEventByUser.getAnnotation();
        String userDescription = updateEventByUser.getDescription();
        LocalDateTime userEventDate = updateEventByUser.getEventDate();
        Boolean userPaid = updateEventByUser.getPaid();
        Integer userParticipantLimit = updateEventByUser.getParticipantLimit();
        Boolean userRequestModeration = updateEventByUser.getRequestModeration();
        UserState userStateAction = updateEventByUser.getStateAction();
        String userTitle = updateEventByUser.getTitle();

        if (userAnnotation != null) {
            eventForUpdate.setAnnotation(userAnnotation);
        }
        if (userCategory != null) {
            eventForUpdate.setCategory(userCategory);
        }
        if (userDescription != null) {
            eventForUpdate.setDescription(userDescription);
        }
        if (userEventDate != null) {
            eventForUpdate.setEventDate(userEventDate);
        }
        if (userLocation != null) {
            eventForUpdate.setLocation(userLocation);
        }
        if (userPaid != null) {
            eventForUpdate.setPaid(userPaid);
        }
        if (userParticipantLimit != null) {
            eventForUpdate.setParticipantLimit(userParticipantLimit);
        }
        if (userRequestModeration != null) {
            eventForUpdate.setRequestModeration(userRequestModeration);
        }
        if (userStateAction != null) {
            switch (userStateAction) {
                case CANCEL_REVIEW:
                    eventForUpdate.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventForUpdate.setState(State.PENDING);
                    break;
            }
        }
        if (userTitle != null) {
            eventForUpdate.setTitle(userTitle);
        }

        return eventForUpdate;
    }

    public Event buildEventForAdminPatch(Event eventForUpdate,
                                         UpdateEventAdminRequest adminRequest,
                                         Category adminCategory,
                                         Location adminLocation) {

        String adminAnnotation = adminRequest.getAnnotation();
        String adminDescription = adminRequest.getDescription();
        LocalDateTime adminEventDate = adminRequest.getEventDate();
        Boolean adminPaid = adminRequest.getPaid();
        Integer adminParticipantLimit = adminRequest.getParticipantLimit();
        Boolean adminRequestModeration = adminRequest.getRequestModeration();
        AdminState adminStateAction = adminRequest.getStateAction(); // А надо ли здесь проверять State?
        String adminTitle = adminRequest.getTitle();

        if (adminAnnotation != null) {
            eventForUpdate.setAnnotation(adminAnnotation);
        }
        if (adminCategory != null) {
            eventForUpdate.setCategory(adminCategory);
        }
        if (adminDescription != null) {
            eventForUpdate.setDescription(adminDescription);
        }
        if (adminEventDate != null) {
            eventForUpdate.setEventDate(adminEventDate);
        }
        if (adminLocation != null) {
            eventForUpdate.setLocation(adminLocation);
        }
        if (adminPaid != null) {
            eventForUpdate.setPaid(adminPaid);
        }
        if (adminParticipantLimit != null) {
            eventForUpdate.setParticipantLimit(adminParticipantLimit);
        }
        if (adminRequestModeration != null) {
            eventForUpdate.setRequestModeration(adminRequestModeration);
        }
        if (adminStateAction != null) {
            switch (adminStateAction) {
                case REJECT_EVENT:
                    eventForUpdate.setState(State.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    eventForUpdate.setState(State.PUBLISHED);
                    eventForUpdate.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }
        if (adminTitle != null) {
            eventForUpdate.setTitle(adminTitle);
        }

        return eventForUpdate;
    }

    public EventShortDto buildEventShortDto(Event event) {
        CategoryDto categoryDto = new CategoryDto(event.getCategory().getId(), event.getCategory().getName());
        UserShortDto userShortDto = new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());

        long confirmedRequests = findConfirmedRequestsForEvent(event.getId());
        long views = loadViewsOfStatistics(event, true);
        log.info("Запрос в сервис статистики обработан успешно. event={}, unique={}, views={}", event, true, views);

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public EventFullDto buildEventFullDto(Event event) {
        CategoryDto categoryDto = new CategoryDto(event.getCategory().getId(), event.getCategory().getName());
        UserShortDto userShortDto = new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());

        long confirmedRequests = findConfirmedRequestsForEvent(event.getId());
        long views = loadViewsOfStatistics(event, true);
        log.info("Запрос в сервис статистики обработан успешно. event={}, unique={}, views={}", event, true, views);


        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userShortDto)
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public List<EventFullDto> buildEventFullDtoList(List<Event> events) {
        return events.stream()
                .map(this::buildEventFullDto)
                .collect(Collectors.toList());
    }

    public List<EventShortDto> buildEventShortDtoList(List<Event> events) {
        return events.stream()
                .map(this::buildEventShortDto)
                .collect(Collectors.toList());
    }

    private long findConfirmedRequestsForEvent(Long eventId) {
        Long countConfirmedEvents = requestService.findByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (countConfirmedEvents == null) {
            return 0;
        }

        return countConfirmedEvents;
    }

    // Загрузка статистики. Загрузка только с момента публикации.
    // Если не опубликован, то 0.
    private long loadViewsOfStatistics(Event event, Boolean unique) {
        if (!event.getState().equals(State.PUBLISHED)) {
            return 0;
        }

        // Ищем по ссылке
        Long eventId = event.getId();

        List<ViewStatsDto> statistics =  httpClient.getStatistics(
                event.getPublishedOn(),
                LocalDateTime.now(),
                "/events/" + eventId,
                unique);

        if (statistics.isEmpty()) {
            return 0;
        }

        return statistics.get(0).getHits();
    }
}
