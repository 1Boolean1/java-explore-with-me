package ru.practicum.explorewithme.server.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.dtos.GetHitDto;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.server.models.Hit;
import ru.practicum.explorewithme.server.repositories.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@Slf4j
public class StatsService implements Service {
    private final StatsRepository repository;

    public StatsService(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GetHitDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            if (unique) {
                log.info("Getting uniq stats from %s to %s(server)", start, end);
                return repository.findUniqByTimestamp(start, end);
            } else {
                log.info("Getting non-uniq stats from %s to %s(server)", start, end);
                return repository.findByTimestamp(start, end);
            }
        } else {
            if (unique) {
                log.info("Getting uniq stats from %s to %s(server) by uris", start, end);
                return repository.findUniqByTimestampAndUris(start, end, uris);
            } else {
                log.info("Getting non-uniq stats from %s to %s(server) by uris", start, end);
                return repository.findByTimestampAndUris(start, end, uris);
            }
        }
    }

    @Transactional
    @Override
    public void saveHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setTime(hitDto.getTime());
        hit.setApp(hitDto.getApp());
        repository.save(hit);
    }
}

