package ru.practicum.explorewithme.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.dtos.RequestDto;
import ru.practicum.explorewithme.main.models.Request;

@UtilityClass
public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getRequester().getId(),
                request.getEvent().getId(),
                request.getStatus()
        );
    }
}
