package ru.practicum.mainservice.user.repository.subscription;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.user.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.model.subscription.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByIdIn(List<Long> ids, Pageable page);

    Optional<Subscription> findByOwnerIdAndSubscriberId(Long userId, Long subscriberId);

    Optional<Subscription> findByOwnerIdAndSubscriberIdAndStatus(Long userId, Long subscriberId, SubscriptionStatus status);

    List<Subscription> findByOwnerIdAndStatus(Long userId, SubscriptionStatus status, Pageable page);

    List<Subscription> findBySubscriberIdAndStatus(Long subscriberId, SubscriptionStatus status, Pageable page);

    List<Subscription> findByOwnerIdAndSubscriberIdInAndStatus(Long userId, Set<Long> subscriberIds, SubscriptionStatus status);

    List<Subscription> findByOwnerIdAndSubscriberIdInAndStatusIn(Long userId, Set<Long> subscriberIds, List<SubscriptionStatus> statuses);
}
