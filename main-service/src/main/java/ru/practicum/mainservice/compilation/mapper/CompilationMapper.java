package ru.practicum.mainservice.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.model.dto.CompilationDto;
import ru.practicum.mainservice.compilation.model.dto.NewCompilationDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.dto.EventShortDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation buildCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        Compilation buildCompilation = Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .build();

        // Обновление поля compilation в events.
        // Это нужно для того, чтобы обновилось поле compilation в таблице events.
        events.forEach(event -> event.setCompilation(buildCompilation));

        return buildCompilation;
    }

    public CompilationDto buildCompilationDto(Compilation createCompilation, List<Event> events) {
        List<EventShortDto> eventShortDtos = eventMapper.buildEventShortDtoList(events);

        return CompilationDto.builder()
                .events(eventShortDtos)
                .id(createCompilation.getId())
                .pinned(createCompilation.getPinned())
                .title(createCompilation.getTitle())
                .build();
    }

    public List<CompilationDto> buildCompilationDtoList(List<Compilation> findCompilations) {
        return findCompilations.stream()
                .map(compilation -> buildCompilationDto(compilation, compilation.getEvents()))
                .collect(Collectors.toList());
    }
}
