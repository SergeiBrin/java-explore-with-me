package ru.practicum.mainservice.user.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.user.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.model.dto.subscription.SubscriptionDto;
import ru.practicum.mainservice.user.model.dto.subscription.UpdateSubscriptionDto;
import ru.practicum.mainservice.user.service.subscription.SubscriptionService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
// Контроллер для взаимодействия пользователя с подписчиками
public class PrivateUserController {
    private final SubscriptionService subscriptionService;

    // Получение пользователем списка заявок на подписку - по статусу
    @GetMapping("/{userId}")
    public ResponseEntity<List<SubscriptionDto>> findSubscriptionByStatus(@PathVariable Long userId,
                                                                          @RequestParam(required = true) SubscriptionStatus status,
                                                                          @RequestParam(defaultValue = "0")
                                                                              @PositiveOrZero int from,
                                                                          @RequestParam(defaultValue = "10")
                                                                              @Positive int size) {
        log.info("Поступил GET запрос в PrivateUserController. " +
                "Метод findSubscriptionByStatus(), userId={}, status={}", userId, status);
        List<SubscriptionDto> findSubscriptions = subscriptionService.findSubscriptionByStatus(userId, status, from, size);

        return new ResponseEntity<>(findSubscriptions, HttpStatus.OK);
    }

    // Подтверждение или отклонение заявок на подписку или отклонение подтвержденных подписок
    @PatchMapping("/{userId}")
    public ResponseEntity<List<SubscriptionDto>> updateSubscriptions(@PathVariable Long userId,
                                                                     @Valid @RequestBody UpdateSubscriptionDto updateSubscriptionDto) {
        log.info("Поступил PATCH запрос в PrivateUserController. " +
                "Метод updateSubscriptions(), userId={}, updateSubscriptionDto={}", userId, updateSubscriptionDto);
        List<SubscriptionDto> updateSubscriptions = subscriptionService.updateSubscriptionStatuses(userId, updateSubscriptionDto);

        return new ResponseEntity<>(updateSubscriptions, HttpStatus.OK);
    }
}
