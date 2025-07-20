package ru.practicum.explorewithme.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.dtos.EventShortDto;
import ru.practicum.explorewithme.main.models.Event;

@UtilityClass
public class EventMapper {
    public static EventDto toDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }
}
