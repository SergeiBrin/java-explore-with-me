package ru.practicum.mainservice.compilation.model.dto;

import lombok.*;
import ru.practicum.mainservice.event.model.dto.EventShortDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CompilationDto {
    private List<EventShortDto> events;

    private Long id;

    private Boolean pinned;

    private String title;
}
