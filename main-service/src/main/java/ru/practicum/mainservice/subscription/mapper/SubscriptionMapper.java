package ru.practicum.mainservice.subscription.mapper;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.subscription.enums.SubscriptionStatus;
import ru.practicum.mainservice.subscription.model.Subscription;
import ru.practicum.mainservice.subscription.model.dto.SubscriptionDto;
import ru.practicum.mainservice.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionMapper {
    public static Subscription buildSubscription(User subscriber, User owner) {
        // Если у пользователя приватный аккаунт, то подписка будет ожидать подтверждения пользователем
        // Иначе подписка сразу же будет одобрена.
        SubscriptionStatus status;
        if (owner.getPrivateAccount()) {
            status = SubscriptionStatus.PENDING;
        } else {
            status = SubscriptionStatus.APPROVED;
        }

        return Subscription.builder()
                .owner(owner)
                .subscriber(subscriber)
                .status(status)
                .build();
    }

    public static List<Subscription> buildSubscriptionsForPatch(List<Subscription> findSubscriptions, SubscriptionStatus status) {
        return findSubscriptions
                .stream()
                .peek(subscription -> subscription.setStatus(status))
                .collect(Collectors.toList());
    }

    @Transactional
    public static SubscriptionDto buildSubscriptionDto(Subscription createSubscription) {
        return SubscriptionDto.builder()
                .id(createSubscription.getId())
                .owner(createSubscription.getOwner())
                .subscriber(createSubscription.getSubscriber())
                .status(createSubscription.getStatus())
                .build();
    }

    @Transactional
    public static List<SubscriptionDto> buildSubscriptionDtoList(List<Subscription> findSubscriptions) {
        return findSubscriptions
                .stream()
                .map(SubscriptionMapper::buildSubscriptionDto)
                .collect(Collectors.toList());
    }
}
