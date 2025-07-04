package ru.practicum.explorewithme.server.services;

import ru.practicum.explorewithme.dto.dtos.GetHitDto;
import ru.practicum.explorewithme.dto.dtos.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface Service {
    List<GetHitDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    void saveHit(HitDto hitDto);
}
