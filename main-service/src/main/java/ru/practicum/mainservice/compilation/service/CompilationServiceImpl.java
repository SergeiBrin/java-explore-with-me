package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.model.dto.CompilationDto;
import ru.practicum.mainservice.compilation.model.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.dto.UpdateCompilationDto;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.utils.PageRequestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Transactional(readOnly = true)
    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation findCompilation = findById(compId);
        log.info("GET запрос в CompilationController обработан успешно. " +
                "Метод findCompilationById(), findCompilation={}", findCompilation);
        List<Event> compilationEvents = findCompilation.getEvents();

        return compilationMapper.buildCompilationDto(findCompilation, compilationEvents);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> findCompilationsByParam(Boolean pinned, int from, int size) {
        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);
        // Если pinned null, то вернет всё.
        List<Compilation> findCompilations = compilationRepository.findByPinnedOrAll(pinned, page);
        log.info("GET запрос в CompilationController обработан успешно. " +
                "Метод findCompilationsByParam(), findCompilations={}", findCompilations);

        return compilationMapper.buildCompilationDtoList(findCompilations);
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Set<Long> eventIds = compilationDto.getEvents();
        List<Event> findEvents = eventRepository.findByIdIn(eventIds);
        Compilation buildCompilation = compilationMapper.buildCompilation(compilationDto, findEvents);

        Compilation createCompilation = compilationRepository.save(buildCompilation);
        log.info("POST запрос в AdminCompilationController обработан успешно. " +
                "Метод createCompilation(), createCompilation={}", createCompilation);

        return compilationMapper.buildCompilationDto(createCompilation, findEvents);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto compilationDto) {
        Compilation buildCompilation = findById(compId);

        List<Event> oldEvents = buildCompilation.getEvents();
        List<Event> newEvents = new ArrayList<>();

        Set<Long> updateEventIds = compilationDto.getEvents();
        Boolean updatePinned = compilationDto.isPinned();
        String updateTitle = compilationDto.getTitle();

        if (buildCompilation.getPinned() != updatePinned) {
            buildCompilation.setPinned(updatePinned);
        }
        if (updateTitle != null) {
            buildCompilation.setTitle(updateTitle);
        }
        if (!updateEventIds.isEmpty()) {
            newEvents = eventRepository.findByIdIn(updateEventIds);
            buildCompilation.setEvents(newEvents);
            updateEventForCompilation(buildCompilation, oldEvents, newEvents);
        }

        Compilation updateCompilation = compilationRepository.save(buildCompilation);
        log.info("UPDATE запрос в AdminCompilationController обработан успешно. " +
                "Метод updateCompilation(), updateCompilation={}", updateCompilation);

        if (newEvents.isEmpty()) {
            return compilationMapper.buildCompilationDto(updateCompilation, oldEvents);
        }

        return compilationMapper.buildCompilationDto(updateCompilation, newEvents);
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        Compilation delCompilation = findById(compId);
        List<Event> compilationEvents = delCompilation.getEvents();
        updateEventForCompilation(compilationEvents);

        compilationRepository.deleteById(compId);
        log.info("DELETE запрос в AdminCompilationController обработан успешно. Метод deleteCompilationById()");
    }

    private Compilation findById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d was not found", compId)));
    }

    // Меняю значения поля compilation в Event-ax, чтобы при save(compilation) -
    // в таблице events эти поля тоже изменились на актуальные.
    private void updateEventForCompilation(Compilation compilation, List<Event> oldEvents, List<Event> newEvents) {
        oldEvents.removeIf(newEvents::contains);
        oldEvents.forEach(event -> event.setCompilation(null));
        newEvents.forEach(event -> event.setCompilation(compilation));
    }

    // Меняю значения поля compilation в Event-ax перед его удалением
    private void updateEventForCompilation(List<Event> events) {
        events.forEach(event -> event.setCompilation(null));
    }
}
