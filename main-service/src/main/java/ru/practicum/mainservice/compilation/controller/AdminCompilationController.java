package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.model.dto.CompilationDto;
import ru.practicum.mainservice.compilation.model.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.dto.UpdateCompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Поступил POST запрос в AdminCompilationController. " +
                "Метод createCompilation(), newCompilationDto={}", compilationDto);
        CompilationDto createCompilation = compilationService.createCompilation(compilationDto);

        return new ResponseEntity<>(createCompilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @Valid @RequestBody UpdateCompilationDto compilationDto) {
        log.info("Поступил PATCH запрос в AdminCompilationController. " +
                "Метод updateCompilation(), compId={}, CompilationDto={}", compId, compilationDto);
        CompilationDto updateCompilation = compilationService.updateCompilation(compId, compilationDto);

        return new ResponseEntity<>(updateCompilation, HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilationById(@PathVariable Long compId) {
        log.info("Поступил DELETE запрос в AdminCompilationController. " +
                "Метод deleteCompilationById(), compId={}", compId);
        compilationService.deleteCompilationById(compId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
