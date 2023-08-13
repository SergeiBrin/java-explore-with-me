package ru.practicum.mainservice.user.model.dto.subscription;

import lombok.*;
import ru.practicum.mainservice.user.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.model.user.User;

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
