package ru.practicum.mainservice.compilation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewCompilationDto {
    private final Set<Long> events = new HashSet<>(); // по спецификации уникальные id

    private boolean pinned; // по дефолту false

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
