package ru.practicum.mainservice.user.service.subscription;

import ru.practicum.mainservice.event.model.dto.EventShortDto;
import ru.practicum.mainservice.user.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.model.dto.subscription.SubscriptionDto;
import ru.practicum.mainservice.user.model.dto.subscription.UpdateSubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto findSubscriptionById(Long subscriberId, Long userId);

    List<SubscriptionDto> findSubscriptionsByIds(List<Long> ids, int from, int size);

    List<SubscriptionDto> findConfirmedSubscriptions(Long subscriberId, int from, int size);

    List<SubscriptionDto> findSubscriptionByStatus(Long userId, SubscriptionStatus status, int from, int size);

    List<EventShortDto> findActualUserEvents(Long subscriberId, Long userId, int from, int size);

    SubscriptionDto createSubscription(Long subscriberId, Long userId);

    SubscriptionDto cancelSubscription(Long subscriberId, Long userId);

    List<SubscriptionDto> updateSubscriptionStatuses(Long userId, UpdateSubscriptionDto updateSubscriptionDto);
}
