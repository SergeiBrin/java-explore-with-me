package ru.practicum.mainservice.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.mainservice.event.annotation.FutureDateTime;
import ru.practicum.mainservice.location.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureDateTime
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private boolean paid; // Дефолт по спецификации false

    @NotNull
    private int participantLimit;  // Дефолт по спецификации = 0

    private boolean requestModeration = true; // Дефолт по спецификации true

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
