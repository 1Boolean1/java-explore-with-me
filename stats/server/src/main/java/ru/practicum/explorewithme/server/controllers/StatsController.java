package ru.practicum.explorewithme.server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.dtos.GetHitDto;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.server.services.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(final StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody HitDto hitDto) {
        log.info("Saving hit: {}(server)", hitDto);
        statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<GetHitDto> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris, @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Getting hits: {}(server)", uris);
        return statsService.getStats(start, end, uris, unique);
    }
}
