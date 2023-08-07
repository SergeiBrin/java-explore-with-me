package ru.practicum.mainservice.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.request.enums.RequestStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParticipationRequestDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") // Так нужно по спецификации
    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    private RequestStatus status;
}
