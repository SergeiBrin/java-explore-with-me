package ru.practicum.mainservice.subscription.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.model.dto.EventShortDto;
import ru.practicum.mainservice.subscription.model.dto.SubscriptionDto;
import ru.practicum.mainservice.subscription.service.SubscriptionService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/subscriptions") // Подумать над названием
@RequiredArgsConstructor
@Slf4j
// Контроллер для подписчика
public class PrivateSubscriptionController {
    private final SubscriptionService subscriptionService;

    // Поиск записи в таблице по id подписчика и id пользователя, на которого он хочет подписаться
    // или уже подписан. Будет полезно, чтобы посмотреть статус подписки
    @GetMapping("/{subscriberId}/{userId}")
    public ResponseEntity<SubscriptionDto> findSubscriptionById(@PathVariable Long subscriberId,
                                                                @PathVariable Long userId) {
        log.info("Поступил GET запрос в PrivateSubscriptionController. " +
                "Метод findSubscriptionById(), subscriberId={}, userId={}", subscriberId, userId);
        SubscriptionDto findSubscription = subscriptionService.findSubscriptionById(subscriberId, userId);

        return new ResponseEntity<>(findSubscription, HttpStatus.OK);
    }

    // Поиск подписок подписчика, одобренных пользователями, которым была сделана заявка.
    @GetMapping("/{subscriberId}/confirmed")
    public ResponseEntity<List<SubscriptionDto>> findConfirmedSubscriptions(@PathVariable Long subscriberId,
                                                                            @RequestParam(defaultValue = "0")
                                                                                @PositiveOrZero int from,
                                                                            @RequestParam(defaultValue = "10")
                                                                                @Positive int size) {
        log.info("Поступил GET запрос в PrivateSubscriptionController. " +
                "Метод findConfirmedSubscriptions(), subscriberId={}", subscriberId);;
        List<SubscriptionDto> findSubscriptions = subscriptionService.findConfirmedSubscriptions(subscriberId, from, size);

        return new ResponseEntity<>(findSubscriptions, HttpStatus.OK);
    }

    // Просмотр актуальных опубликованных событий пользователя, на которого подписан подписчик
    @GetMapping("/{subscriberId}/{userId}/events")
    public ResponseEntity<List<EventShortDto>> findActualUserEvents(@PathVariable Long subscriberId,
                                                                    @PathVariable Long userId,
                                                                    @RequestParam(defaultValue = "0")
                                                                        @PositiveOrZero int from,
                                                                    @RequestParam(defaultValue = "10")
                                                                        @Positive int size) {
        log.info("Поступил GET запрос в PrivateSubscriptionController. " +
                "Метод findActualUserEvents(), subscriberId={}, userId={}", subscriberId, userId);
        List<EventShortDto> findEvents = subscriptionService.findActualUserEvents(subscriberId, userId, from, size);

        return new ResponseEntity<>(findEvents, HttpStatus.OK);
    }

    // Подача заявки на подписку
    @PostMapping("/{subscriberId}/{userId}")
    public ResponseEntity<SubscriptionDto> createSubscription(@PathVariable Long subscriberId,
                                                              @PathVariable Long userId) {
        log.info("Поступил POST запрос в PrivateSubscriptionController. " +
                "Метод createSubscription(), subscriberId={}, userId={}", subscriberId, userId);
        SubscriptionDto createSubscription = subscriptionService.createSubscription(subscriberId, userId);

        return new ResponseEntity<>(createSubscription, HttpStatus.CREATED);
    }

    // Отмена подписки
    @PatchMapping("/{subscriberId}/{userId}/cancel")
    public ResponseEntity<SubscriptionDto> cancelSubscription(@PathVariable Long subscriberId,
                                                              @PathVariable Long userId) {
        log.info("Поступил PATCH запрос в PrivateSubscriptionController. " +
                "Метод cancelSubscription(), subscriberId={}, userId={}", subscriberId, userId);
        SubscriptionDto cancelSubscription = subscriptionService.cancelSubscription(subscriberId, userId);

        return new ResponseEntity<>(cancelSubscription, HttpStatus.OK);
    }
}
