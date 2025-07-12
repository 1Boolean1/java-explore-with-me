package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.EventCreateDto;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.dtos.UpdateEventDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.EventMapper;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.models.State;
import ru.practicum.explorewithme.main.repositories.CategoryRepository;
import ru.practicum.explorewithme.main.repositories.EventRepository;
import ru.practicum.explorewithme.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventDto save(Long id, EventCreateDto eventCreateDto) {
        if (eventCreateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("Event date is before 2 hours from now");
            throw new BadRequestException("Event date is before now");
        }
        if (eventCreateDto.getTitle().isBlank()) {
            log.error("Title is blank");
            throw new BadRequestException("Title is blank");
        }
        if (categoryRepository.findById(eventCreateDto.getCategoryId().intValue()).isEmpty()) {
            log.error("Category not found");
            throw new NotFoundException("Category not found");
        }
        if (userRepository.findById(id.intValue()).isEmpty()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        Event newEvent = new Event();
        newEvent.setAnnotation(eventCreateDto.getAnnotation());
        newEvent.setTitle(eventCreateDto.getTitle());
        newEvent.setCategory(categoryRepository.findById(eventCreateDto.getCategoryId().intValue()).get());
        newEvent.setEventDate(eventCreateDto.getEventDate());
        newEvent.setDescription(eventCreateDto.getDescription());
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setPaid(eventCreateDto.getPaid());
        newEvent.setLocation(eventCreateDto.getLocation());
        newEvent.setParticipantLimit(eventCreateDto.getParticipantLimit());
        newEvent.setInitiator(userRepository.findById(id.intValue()).get());
        newEvent.setState(State.PUBLISHED);
        newEvent.setConfirmedRequests(0);
        newEvent.setRequestModeration(true);
        newEvent.setViews(0L);

        return EventMapper.toDto(eventRepository.save(newEvent));
    }

    public EventDto getByUserIdAndEventId(Long userId, Long eventId) {
        if (userRepository.findById(userId.intValue()).isEmpty()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event not found");
            throw new NotFoundException("Event not found");
        }
        if (eventRepository.findById(eventId).get().getInitiator().getId()
                != userId.intValue()) {
            log.error("Initiator not found");
            throw new NotFoundException("Initiator not found");
        }
        return EventMapper.toDto(eventRepository.findById(eventId).get());
    }

    public List<EventDto> getEventsByUserId(Long userId) {
        if (userRepository.findById(userId.intValue()).isEmpty()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        return eventRepository.findEventDtoByInitiatorId(userId);
    }

    @Transactional
    public EventDto patchEvent(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event does not exist"));
        if (existingEvent.getInitiator().getId() != userId.intValue()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        return updateEvent(existingEvent, updateEventDto);
    }

    public EventDto getByEventId(Long eventId) {
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event not found");
            throw new NotFoundException("Event not found");
        }
        return EventMapper.toDto(eventRepository.findById(eventId).get());
    }

    public List<EventDto> getOpenEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        Integer from,
                                        Integer size) {
        if (text == null) {
            text = "";
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        if (rangeEnd.isBefore(rangeStart)) {
            log.error("rangeEnd is before rangeStart");
            throw new BadRequestException("rangeEnd is before rangeStart");
        }

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        switch (sort) {
            case "EVENT_DATE":
                if (onlyAvailable) {
                    return eventRepository.getOpenAvailableEventsSortsByEventDate(text, categories, paid, rangeStart, rangeEnd, pageRequest).stream().map(EventMapper::toDto).toList();
                } else {
                    return eventRepository.getOpenUnAvailableEventsSortsByEventDate(text, categories, paid, rangeStart, rangeEnd, pageRequest).stream().map(EventMapper::toDto).toList();
                }
            case "VIEWS":
                if (onlyAvailable) {
                    return eventRepository.getOpenAvailableEventsSortsByViews(text, categories, paid, rangeStart, rangeEnd, pageRequest).stream().map(EventMapper::toDto).toList();
                } else {
                    return eventRepository.getOpenUnAvailableEventsSortsByViews(text, categories, paid, rangeStart, rangeEnd, pageRequest).stream().map(EventMapper::toDto).toList();
                }
            default:
                log.error("Invalid sort value");
                throw new BadRequestException("Invalid sort value");
        }
    }

    public List<EventDto> getEvents(List<Long> usersIds,
                                    List<State> states,
                                    List<Long> categoriesIds,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Integer from,
                                    Integer size) {

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        if (rangeEnd.isBefore(rangeStart)) {
            log.error("rangeEnd is before rangeStart");
            throw new BadRequestException("rangeEnd is before rangeStart");
        }

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        return eventRepository.getAllEvents(usersIds, states, categoriesIds, rangeStart, rangeEnd, pageRequest).stream().map(EventMapper::toDto).toList();
    }

    @Transactional
    public EventDto patchEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event does not exist"));
        return updateEvent(existingEvent, updateEventDto);
    }

    private EventDto updateEvent(Event existingEvent, UpdateEventDto updateEventDto) {
        Boolean needsUpdate = false;
        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank()) {
            if (!existingEvent.getAnnotation().equals(updateEventDto.getAnnotation())) {
                log.info("Annotation changed to " + updateEventDto.getAnnotation());
                existingEvent.setAnnotation(updateEventDto.getAnnotation());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getCategory() != null && updateEventDto.getCategory() != 0) {
            if (categoryRepository.findById(updateEventDto.getCategory().intValue()).isEmpty()) {
                log.error("Category not found");
                throw new NotFoundException("Category not found");
            }
            if (existingEvent.getCategory().equals(categoryRepository.findById(updateEventDto.getCategory().intValue()).get())) {
                log.info("Category changed to " + updateEventDto.getCategory());
                existingEvent.setCategory(categoryRepository.findById(updateEventDto.getCategory().intValue()).get());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank()) {
            if (!existingEvent.getDescription().equals(updateEventDto.getDescription())) {
                log.info("Description changed to " + updateEventDto.getDescription());
                existingEvent.setDescription(updateEventDto.getDescription());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.error("Event date is before 2 hours from now");
                throw new BadRequestException("Event date is before now");
            }
            if (!existingEvent.getEventDate().equals(updateEventDto.getEventDate())) {
                log.info("EventDate changed to " + updateEventDto.getEventDate());
                existingEvent.setEventDate(updateEventDto.getEventDate());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getLocation() != null) {
            if (!existingEvent.getLocation().equals(updateEventDto.getLocation())) {
                log.info("Location changed to " + updateEventDto.getLocation());
                existingEvent.setLocation(updateEventDto.getLocation());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getPaid() != null) {
            if (!existingEvent.getPaid().equals(updateEventDto.getPaid())) {
                log.info("Paid changed to " + updateEventDto.getPaid());
                existingEvent.setPaid(updateEventDto.getPaid());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getParticipantLimit() != null) {
            if (existingEvent.getParticipantLimit() != updateEventDto.getParticipantLimit()) {
                log.info("ParticipantLimit changed to " + updateEventDto.getParticipantLimit());
                existingEvent.setParticipantLimit(updateEventDto.getParticipantLimit());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getRequestModeration() != null) {
            if (existingEvent.getRequestModeration() != updateEventDto.getRequestModeration()) {
                log.info("RequestModeration changed to " + updateEventDto.getRequestModeration());
                existingEvent.setRequestModeration(updateEventDto.getRequestModeration());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getStateAction() != null) {
            if (existingEvent.getState() != updateEventDto.getStateAction()) {
                log.info("StateAction changed to " + updateEventDto.getStateAction());
                existingEvent.setState(updateEventDto.getStateAction());
                needsUpdate = true;
            }
        }

        if (updateEventDto.getTitle() != null) {
            if (existingEvent.getTitle() != updateEventDto.getTitle()) {
                log.info("Title changed to " + updateEventDto.getTitle());
                existingEvent.setTitle(updateEventDto.getTitle());
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            Event updatedEvent = eventRepository.save(existingEvent);
            return EventMapper.toDto(updatedEvent);
        } else {
            return EventMapper.toDto(existingEvent);
        }
    }
}
