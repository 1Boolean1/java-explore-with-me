package ru.practicum.explorewithme.server.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.server.models.Hit;

@UtilityClass
public class HitMapper {
    public Hit mapToHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTime());
    }
}
