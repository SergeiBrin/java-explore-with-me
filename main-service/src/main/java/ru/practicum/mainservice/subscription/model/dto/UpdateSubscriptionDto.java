package ru.practicum.mainservice.subscription.model.dto;

import lombok.*;
import ru.practicum.mainservice.subscription.enums.SubscriptionStatus;

import javax.validation.constraints.NotNull;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateSubscriptionDto {
    @NotNull
    private Set<Long> subscriberIds;

    @NotNull
    private SubscriptionStatus status;
}
