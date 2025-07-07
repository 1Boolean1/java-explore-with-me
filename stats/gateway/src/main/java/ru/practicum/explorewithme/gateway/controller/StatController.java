package ru.practicum.explorewithme.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.gateway.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public void saveHit(@RequestBody HitDto hitDto) {
        log.info("Saving hit: {}(gateway)", hitDto);
        statsClient.saveStats(hitDto.getApp(), hitDto.getUri(), hitDto.getIp());
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(required = false) List<String> uris, @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Retrieving stats for {}(gateway)", uris);
        return statsClient.getStat(start, end, uris, unique);
    }
}
