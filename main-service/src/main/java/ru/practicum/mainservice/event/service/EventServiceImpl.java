package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.service.CategoryService;
import ru.practicum.mainservice.event.enums.AdminState;
import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.dto.*;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.model.ConflictException;
import ru.practicum.mainservice.exception.model.InvalidDateTimeException;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.location.service.LocationService;
import ru.practicum.mainservice.user.model.user.User;
import ru.practicum.mainservice.user.service.user.UserService;
import ru.practicum.mainservice.utils.PageRequestFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findEventsByUserId(Long userId, int from, int size) {
        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);
        List<Event> findEventByUser = eventRepository.findByInitiatorId(userId, page);
        log.info("GET запрос в PrivateEventController обработан успешно. " +
                "Метод findEventsByUserId(), findEventByUser={}", findEventByUser);

        return eventMapper.buildEventShortDtoList(findEventByUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> findEventsByParameters(List<Long> users,
                                                     List<State> states,
                                                     List<Long> categories,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     int from,
                                                     int size) {
        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);
        List<Event> findEventsByParam;

        if (rangeStart != null && rangeEnd != null) {
            checkStartBeforeEnd(rangeStart, rangeEnd);
            findEventsByParam = eventRepository
                    .findEventsByParametersWithTime(users, states, categories, rangeStart, rangeEnd, page);
            log.info("GET запрос в AdminEventController обработан успешно. " +
                    "Метод findEventsByParameters(), findEventsByParam={}", findEventsByParam);
        } else {
            findEventsByParam = eventRepository.findEventsByParametersWithoutTime(users, states, categories, page);
            log.info("GET запрос в AdminEventController обработан успешно. " +
                    "Метод findEventsByParameters(), findEventsByParam={}", findEventsByParam);
        }

        return eventMapper.buildEventFullDtoList(findEventsByParam);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findPublishedEventsByParameters(String text,
                                                               List<Long> categories,
                                                               Boolean paid,
                                                               LocalDateTime rangeStart,
                                                               LocalDateTime rangeEnd,
                                                               Boolean onlyAvailable,
                                                               String sort,
                                                               int from,
                                                               int size) {
        List<Event> findEventsByParameters;
        Pageable page;

        if (sort != null && sort.equals("EVENT_DATE")) {
            page = PageRequestFactory.buildPageRequestWithSort(from, size, Sort.by("eventDate").ascending());
        } else {
            page = PageRequestFactory.buildPageRequestWithoutSort(from, size);
        }

        if (rangeStart != null && rangeEnd != null) {
            checkStartBeforeEnd(rangeStart, rangeEnd);
            findEventsByParameters = eventRepository.findPublishedEventsByParameters(
                    text,
                    categories,
                    paid,
                    rangeStart,
                    rangeEnd,
                    onlyAvailable,
                    page);
            log.info("GET запрос в PublicEventController обработан успешно. " +
                    "Метод findPublishedEventsByParameters(), findEventsByParameters={}", findEventsByParameters);
        } else  {
            findEventsByParameters = eventRepository.findPublishedEventsByParametersWithTimeNow(
                    text,
                    categories,
                    paid,
                    onlyAvailable,
                    page);
            log.info("GET запрос в PublicEventController обработан успешно. " +
                    "Метод findPublishedEventsByParameters(), findEventsByParameters={}", findEventsByParameters);
        }

        // Здесь!!!! Если Sort Views, то сортировать по views
        List<EventShortDto> buildEventShortDto = eventMapper.buildEventShortDtoList(findEventsByParameters);

        if (sort != null && sort.equals("VIEWS")) {
            List<EventShortDto> sortedByViews = sortByViews(buildEventShortDto);
            log.info("sortedByViews={} отсортирован по просмотрам", sortedByViews);

            return sortedByViews;
        }

        return buildEventShortDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto findEventById(Long eventId) {
        Event event = findPublishedEventById(eventId);
        log.info("GET запрос в PublicEventController обработан успешно. " +
                "Метод findEventById(), event={}", event);

        return eventMapper.buildEventFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId) {
        userService.findUserById(userId);
        findEvent(eventId);

        Event findEvent = findEventByIdAndInitiatorId(userId, eventId);
        log.info("GET запрос в PrivateEventController обработан успешно. " +
                "Метод findEventByUserIdAndEventId(), findEvent={}", findEvent);

        return eventMapper.buildEventFullDto(findEvent);
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userService.findUserById(userId);
        Category category = categoryService.findCategoryById(newEventDto.getCategory());
        Location location = addLocation(newEventDto.getLocation());

        Event buildEvent = eventMapper.buildEventForPost(initiator, category, location, newEventDto);
        Event createEvent = eventRepository.save(buildEvent);
        log.info("POST запрос в PrivateEventController обработан успешно. " +
                "Метод createEvent(), createEvent={}", createEvent);

        return eventMapper.buildEventFullDto(createEvent);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventByUser) {
        Event event = findEvent(eventId);

        checkEventTimeBeforeUpdateByUser(event);
        checkEventStateBeforeUpdateByUser(event);

        Category userCategory = checkAdminCategory(updateEventByUser.getCategory());
        Location userLocation = checkAdminLocation(updateEventByUser.getLocation());

        Event buildEvent = eventMapper.buildEventForUserPatch(event, updateEventByUser, userCategory, userLocation);
        Event updateEvent = eventRepository.save(buildEvent);
        log.info("PATCH запрос в PrivateEventController обработан успешно. " +
                "Метод updateEventByUser(), updateEvent={}", updateEvent);

        return eventMapper.buildEventFullDto(updateEvent);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventByAdmin) {
        Event event = findEvent(eventId); // Здесь ManyToOne без Lazy, иначе не выгружаются категории и локация.

        checkEventTimeBeforeUpdateByAdmin(event, updateEventByAdmin);
        checkEventStateBeforeUpdateByAdmin(event, updateEventByAdmin);

        Category adminCategory = checkAdminCategory(updateEventByAdmin.getCategory());
        Location adminLocation = checkAdminLocation(updateEventByAdmin.getLocation());

        Event buildEvent = eventMapper.buildEventForAdminPatch(event, updateEventByAdmin, adminCategory, adminLocation);
        Event updateEvent = eventRepository.save(buildEvent);
        log.info("PATCH запрос в AdminEventController обработан успешно. " +
                "Метод updateEventByAdmin(), updateEvent={}", updateEvent);

        return eventMapper.buildEventFullDto(updateEvent);
    }

    private User findUserById(Long userId) {
        return userService.findUserById(userId);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Event findPublishedEventById(Long eventId) {
        return eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d must be published", eventId)));
    }

    private Event findEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Location addLocation(Location location) {
        return locationService.addLocation(location);
    }

    private void checkEventTimeBeforeUpdateByUser(Event event) {
        LocalDateTime eventDate = event.getEventDate();

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidDateTimeException("Date and time cannot be earlier than two hour from the current moment");
        }
    }

    private void checkEventStateBeforeUpdateByUser(Event event) {
    State state = event.getState();
    // 409
       if (state.equals(State.PUBLISHED)) {
           throw new ConflictException("Only pending or canceled events can be changed");
       }
    }

    // Проверка времени при апдейте админом
    private void checkEventTimeBeforeUpdateByAdmin(Event event, UpdateEventAdminRequest adminRequest) {
        LocalDateTime eventDate = event.getEventDate();

        // Проверка, что до начала события больше одного часа.
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidDateTimeException("Date and time cannot be earlier than one hour from the current moment");
        }
    }

    // Проверка состояния при апдейте админом
    private void checkEventStateBeforeUpdateByAdmin(Event event, UpdateEventAdminRequest adminRequest) {
        State eventState = event.getState();
        AdminState adminState = adminRequest.getStateAction();

        // Если обновляемый adminState null, то проверять, в каком состоянии находится событие
        // не имеет смысла.
        if (adminState == null) {
            return;
        }

        // Проверка состояния Event перед публикацией или отклонением события админом.
        // Тут нужны коды 409
        switch (adminState) {
            case REJECT_EVENT:
                if (eventState.equals(State.PUBLISHED)) {
                    throw new ConflictException("Can't rejected event because it's already published");
                }
            case PUBLISH_EVENT:
                if (!eventState.equals(State.PENDING)) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED");
                }
        }
    }

    private Category checkAdminCategory(Long adminCategoryId) {
        if (adminCategoryId == null) {
            return null;
        }

        return categoryService.findCategoryById(adminCategoryId);
    }

    private Location checkAdminLocation(Location location) {
        if (location == null) {
            return null;
        }

        return locationService.addLocation(location);
    }

    private void checkStartBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new InvalidDateTimeException("Invalid date range: end date must be after start date");
        }
    }

    // Сортировка по просмотрам
    private List<EventShortDto> sortByViews(List<EventShortDto> events) {
        Comparator<EventShortDto> comparator  = Comparator.comparingLong(EventShortDto::getViews);
        events.sort(comparator);
        return events;
    }
}
