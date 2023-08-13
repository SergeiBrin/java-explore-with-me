package ru.practicum.mainservice.request.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.request.enums.RequestStatus;
import ru.practicum.mainservice.user.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event")
    @JsonIgnore
    @ToString.Exclude
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester")
    @JsonIgnore
    @ToString.Exclude
    private User requester;

    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
