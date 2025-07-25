package ru.practicum.explorewithme.server.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.server.models.Hit;

@UtilityClass
public class HitMapper {
    public static Hit mapToHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTime());
    }

    public static HitDto mapToDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTime()
        );
    }
}
