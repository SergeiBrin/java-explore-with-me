package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.model.dto.CompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> findCompilationById(@PathVariable Long compId) {
        log.info("Поступил GET запрос в CompilationController. " +
                "Метод findCompilationById(), compId={}", compId);
        CompilationDto findCompilation = compilationService.findCompilationById(compId);

        return new ResponseEntity<>(findCompilation, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> findCompilations(@RequestParam(required = false) Boolean pinned,
                                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил GET запрос в CompilationController. Метод findCompilations(), pinned={}", pinned);
        List<CompilationDto> findCompilations = compilationService.findCompilationsByParam(pinned, from, size);

        return new ResponseEntity<>(findCompilations, HttpStatus.OK);
    }
}
