package ru.practicum.mainservice.subscription.model.dto;

import lombok.*;
import ru.practicum.mainservice.subscription.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubscriptionDto {
    private Long id;

    private User owner;

    private User subscriber;

    private SubscriptionStatus status;
}
