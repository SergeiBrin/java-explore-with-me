package ru.practicum.mainservice.user.model.dto.subscription;

import lombok.*;
import ru.practicum.mainservice.user.enums.SubscriptionStatus;

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
