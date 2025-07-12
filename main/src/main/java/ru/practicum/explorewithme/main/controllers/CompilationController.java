package ru.practicum.explorewithme.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.gateway.client.StatsClient;
import ru.practicum.explorewithme.main.dtos.AddCompilationDto;
import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.dtos.UpdateCompilationDto;
import ru.practicum.explorewithme.main.services.CompilationService;

import java.util.List;

@RestController
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;
    private final StatsClient statsClient;

    public CompilationController(final CompilationService compilationService, StatsClient statsClient) {
        this.compilationService = compilationService;
        this.statsClient = statsClient;
    }

    @PostMapping("/admin/compilations")
    public CompilationDto addCompilation(@RequestBody final AddCompilationDto compilationDto,
                                         HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Add compilation: {}", compilationDto);
        return compilationService.addCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{id}")
    public void deleteCompilation(@PathVariable final Long id,
                                  HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Delete compilation: {}", id);
        compilationService.deleteCompilation(id);
    }

    @PatchMapping("/admin/compilation/{id}")
    public CompilationDto patchCompilation(@PathVariable final Long id,
                                           @RequestBody final UpdateCompilationDto updateCompilationDto,
                                           HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("update compilation: {}", id);
        return compilationService.updateCompilation(id, updateCompilationDto);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("get compilations: {}", from + size);
        return compilationService.getCompilations(pinned, from, size);

    }

    @GetMapping("/compilations/{id}")
    public CompilationDto getCompilation(@PathVariable final Long id,
                                         HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("get compilation: {}", id);
        return compilationService.getCompilation(id);
    }
}
