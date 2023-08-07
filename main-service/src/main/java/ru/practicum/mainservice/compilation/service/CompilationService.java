package ru.practicum.mainservice.compilation.service;

import ru.practicum.mainservice.compilation.model.dto.CompilationDto;
import ru.practicum.mainservice.compilation.model.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto findCompilationById(Long compId);

    List<CompilationDto> findCompilationsByParam(Boolean pinned, int from, int size);

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto compilationDto);

    void deleteCompilationById(Long compId);
}
