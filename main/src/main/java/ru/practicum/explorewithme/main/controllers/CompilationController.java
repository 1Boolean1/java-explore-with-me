package ru.practicum.explorewithme.main.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dtos.AddCompilationDto;
import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.dtos.UpdateCompilationDto;
import ru.practicum.explorewithme.main.services.CompilationService;

import java.util.List;

@RestController
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    public CompilationController(final CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid final AddCompilationDto compilationDto) {
        log.info("Add compilation: {}", compilationDto);
        return compilationService.addCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable final Long id) {
        log.info("Delete compilation: {}", id);
        compilationService.deleteCompilation(id);
    }

    @PatchMapping("/admin/compilations/{id}")
    public CompilationDto patchCompilation(@PathVariable final Long id,
                                           @RequestBody @Valid final UpdateCompilationDto updateCompilationDto) {
        log.info("update compilation: {}", id);
        return compilationService.updateCompilation(id, updateCompilationDto);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) boolean pinned,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("get compilations: {}", from + size);
        return compilationService.getCompilations(pinned, from, size);

    }

    @GetMapping("/compilations/{id}")
    public CompilationDto getCompilation(@PathVariable final Long id) {
        log.info("get compilation: {}", id);
        return compilationService.getCompilation(id);
    }
}
