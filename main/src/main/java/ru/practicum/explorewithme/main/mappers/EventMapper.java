package ru.practicum.explorewithme.main.mappers;

import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.models.Event;

public class EventMapper {
    public static EventDto toDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getInitiator(),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }
}
