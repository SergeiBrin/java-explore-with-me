package ru.practicum.mainservice.subscription.model;

import lombok.*;
import ru.practicum.mainservice.subscription.enums.SubscriptionStatus;
import ru.practicum.mainservice.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    @ToString.Exclude
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber")
    @ToString.Exclude
    private User subscriber;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
}
