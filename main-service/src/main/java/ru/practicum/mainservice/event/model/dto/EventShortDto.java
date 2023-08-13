package ru.practicum.mainservice.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.category.model.dto.CategoryDto;
import ru.practicum.mainservice.user.model.dto.user.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventShortDto {
    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
