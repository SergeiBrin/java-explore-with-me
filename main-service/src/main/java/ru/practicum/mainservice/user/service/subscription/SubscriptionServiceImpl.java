package ru.practicum.mainservice.user.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.dto.EventShortDto;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.model.ConflictException;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.user.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.mapper.SubscriptionMapper;
import ru.practicum.mainservice.user.model.dto.subscription.SubscriptionDto;
import ru.practicum.mainservice.user.model.dto.subscription.UpdateSubscriptionDto;
import ru.practicum.mainservice.user.model.subscription.Subscription;
import ru.practicum.mainservice.user.model.user.User;
import ru.practicum.mainservice.user.repository.subscription.SubscriptionRepository;
import ru.practicum.mainservice.user.service.user.UserService;
import ru.practicum.mainservice.utils.PageRequestFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    @Override
    public SubscriptionDto findSubscriptionById(Long subscriberId, Long userId) {
        userService.findUserById(subscriberId);
        userService.findUserById(userId);

        Subscription findSubscription = subscriptionRepository.findByOwnerIdAndSubscriberId(userId, subscriberId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "User with id=%d did not apply for a subscription to the user with id=%d", subscriberId, userId)));
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                        "Метод findSubscriptionById(), subscriberId={}, userId={}, findSubscriptions={}",
                subscriberId, userId, findSubscription);

        return SubscriptionMapper.buildSubscriptionDto(findSubscription);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubscriptionDto> findSubscriptionsByIds(List<Long> ids, int from, int size) {
        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);

        List<Subscription> findSubscriptions = subscriptionRepository.findByIdIn(ids, page);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                "Метод findSubscriptionsByIds(), ids={}, findSubscriptions={}", ids, findSubscriptions);

        return SubscriptionMapper.buildSubscriptionDtoList(findSubscriptions);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubscriptionDto> findConfirmedSubscriptions(Long subscriberId, int from, int size) {
        userService.findUserById(subscriberId);

        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);
        List<Subscription> findSubscriptions = subscriptionRepository
                .findBySubscriberIdAndStatus(subscriberId, SubscriptionStatus.APPROVED, page);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                "Метод findConfirmedSubscriptions(), subscriberId={}, findSubscriptions={}", subscriberId, findSubscriptions);

        return SubscriptionMapper.buildSubscriptionDtoList(findSubscriptions);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubscriptionDto> findSubscriptionByStatus(Long userId, SubscriptionStatus status, int from, int size) {
        User user = userService.findUserById(userId);
        boolean isPrivateAccount = user.getPrivateAccount();
        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);

        // Отмененную подписчиком заявку к пользователю, пользователь не сможет посмотреть.
        if (status.equals(SubscriptionStatus.CANCEL)) {
            throw new IllegalArgumentException("Incorrect SubscriptionStatus value");
        }

        // Если у пользователя не приватный аккаунт, то он не может смотреть список пользователей в ожидании подписки.
        if (!isPrivateAccount && status.equals(SubscriptionStatus.PENDING)) {
            throw new IllegalArgumentException("Incorrect SubscriptionStatus value");
        }

        List<Subscription> findSubscriptions = subscriptionRepository.findByOwnerIdAndStatus(userId, status, page);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                        "Метод findSubscriptionByStatus(), userId={}, status={}, findSubscriptions={}",
                userId, status, findSubscriptions);

        return SubscriptionMapper.buildSubscriptionDtoList(findSubscriptions);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findActualUserEvents(Long subscriberId, Long userId, int from, int size) {
        userService.findUserById(subscriberId);
        userService.findUserById(userId);
        subscriptionRepository.findByOwnerIdAndSubscriberIdAndStatus(userId, subscriberId, SubscriptionStatus.APPROVED)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "User with id=%d did not apply for a subscription to the user with id=%d", subscriberId, userId)));

        Pageable page = PageRequestFactory.buildPageRequestWithSort(from, size, Sort.by("eventDate"));
        List<Event> findEvents = eventRepository.findByInitiatorIdAndStateAndEventDateAfter(
                userId,
                State.PUBLISHED,
                LocalDateTime.now(),
                page);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                "Метод findActualUserEvents(), subscriberId={}, userId={}, findEvents={}", subscriberId, userId, findEvents);

        return eventMapper.buildEventShortDtoList(findEvents);
    }

    @Transactional
    @Override
    public SubscriptionDto createSubscription(Long subscriberId, Long userId) {
        User user = userService.findUserById(userId);
        User subscriber = userService.findUserById(subscriberId);

        Subscription buildSubscription = SubscriptionMapper.buildSubscription(subscriber, user);
        Subscription createSubscription = subscriptionRepository.save(buildSubscription);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                "Метод createSubscription(), subscriberId={}, userId={}, createSubscription={}",
                subscriberId, userId, createSubscription);

        return SubscriptionMapper.buildSubscriptionDto(createSubscription);
    }

    @Transactional
    @Override
    public SubscriptionDto cancelSubscription(Long subscriberId, Long userId) {
        userService.findUserById(subscriberId);
        userService.findUserById(userId);

        Subscription findSubscription = subscriptionRepository.findByOwnerIdAndSubscriberId(userId, subscriberId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "User with id=%d did not apply for a subscription to the user with id=%d", subscriberId, userId)));

        boolean status = findSubscription.getStatus().equals(SubscriptionStatus.CANCEL);
        if (status) {
            throw new ConflictException("Subscription already canceled");
        }

        findSubscription.setStatus(SubscriptionStatus.CANCEL);
        Subscription cancelSubscription = subscriptionRepository.save(findSubscription);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                        "Метод cancelSubscription(), subscriberId={}, userId={}, cancelSubscription={}",
                subscriberId, userId, cancelSubscription);

        return SubscriptionMapper.buildSubscriptionDto(cancelSubscription);
    }

    @Transactional
    @Override
    public List<SubscriptionDto> updateSubscriptionStatuses(Long userId, UpdateSubscriptionDto updateSubscriptionDto) {
        userService.findUserById(userId);
        List<Subscription> findSubscriptions = new ArrayList<>();

        Set<Long> subscriberIds = updateSubscriptionDto.getSubscriberIds();
        SubscriptionStatus updateStatus = updateSubscriptionDto.getStatus();

        switch (updateStatus) {
            case APPROVED: // Подтвердить подписку можно только в статусе ожидания
                findSubscriptions = subscriptionRepository
                        .findByOwnerIdAndSubscriberIdInAndStatus(
                                userId,
                                subscriberIds,
                                SubscriptionStatus.PENDING);
                break;
            case REJECTED: // Отклонить подписку можно в статусе ожидания и в статусе подтвержденной.
                findSubscriptions = subscriptionRepository
                        .findByOwnerIdAndSubscriberIdInAndStatusIn(
                                userId,
                                subscriberIds,
                                List.of(SubscriptionStatus.PENDING, SubscriptionStatus.APPROVED));
                break;
            default:
                throw new ConflictException("Incorrect SubscriptionStatus value");
        }

        if (findSubscriptions.size() < subscriberIds.size()) {
            throw new ConflictException("Unable to update subscription statuses due to inappropriate statuses");
        }

        List<Subscription> buildSubscriptionsForPatch = SubscriptionMapper
                .buildSubscriptionsForPatch(findSubscriptions, updateSubscriptionDto.getStatus());

        List<Subscription> updateSubscriptions = subscriptionRepository.saveAll(buildSubscriptionsForPatch);
        log.info("Запрос в SubscriptionServiceImpl обработан успешно. " +
                "Метод updateSubscriptionStatuses(), userId={}, updateSubscriptionDto={}, updateSubscriptions={}",
                userId, updateSubscriptionDto, updateSubscriptions);

        return SubscriptionMapper.buildSubscriptionDtoList(updateSubscriptions);
    }
}

