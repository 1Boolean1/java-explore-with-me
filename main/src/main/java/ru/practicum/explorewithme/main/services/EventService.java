package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.gateway.client.StatsClient;
import ru.practicum.explorewithme.main.dtos.EventCreateDto;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.dtos.UpdateEventDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.ExistsException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.EventMapper;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.models.State;
import ru.practicum.explorewithme.main.repositories.CategoryRepository;
import ru.practicum.explorewithme.main.repositories.EventRepository;
import ru.practicum.explorewithme.main.repositories.LocationRepository;
import ru.practicum.explorewithme.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository, LocationRepository locationRepository, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.statsClient = statsClient;
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
        if (eventCreateDto.getCategory() != null) {
            if (categoryRepository.findById(eventCreateDto.getCategory().intValue()).isEmpty()) {
                log.error("Category not found");
                throw new NotFoundException("Category not found");
            }
        } else {
            log.error("Category is empty");
            throw new BadRequestException("Category is empty");
        }
        if (eventCreateDto.getDescription() == null) {
            log.error("Description is null");
            throw new BadRequestException("Description is null");
        }
        if (eventCreateDto.getDescription().isBlank()) {
            log.error("Description is blank");
            throw new BadRequestException("Description is blank");
        }
        if (eventCreateDto.getAnnotation() == null) {
            log.error("Annotation is null");
            throw new BadRequestException("Annotation is null");
        }
        if (eventCreateDto.getAnnotation().isBlank()) {
            log.error("Annotation is blank");
            throw new BadRequestException("Annotation is blank");
        }
        if (eventCreateDto.getParticipantLimit() < 0) {
            log.error("Participant limit is negative");
            throw new BadRequestException("Participant limit is negative");
        }
        if (userRepository.findById(id.intValue()).isEmpty()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        Event newEvent = new Event();
        newEvent.setAnnotation(eventCreateDto.getAnnotation());
        newEvent.setTitle(eventCreateDto.getTitle());
        if (eventCreateDto.getCategory() != null) {
            newEvent.setCategory(categoryRepository.findById(eventCreateDto.getCategory().intValue()).get());
        }
        newEvent.setEventDate(eventCreateDto.getEventDate());
        newEvent.setDescription(eventCreateDto.getDescription());
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setPaid(eventCreateDto.getPaid());
        locationRepository.save(eventCreateDto.getLocation());
        newEvent.setLocation(eventCreateDto.getLocation());
        newEvent.setParticipantLimit(eventCreateDto.getParticipantLimit());
        newEvent.setInitiator(userRepository.findById(id.intValue()).get());
        newEvent.setState(State.PENDING);
        newEvent.setConfirmedRequests(0);
        newEvent.setRequestModeration(eventCreateDto.getRequestModeration());
        newEvent.setViews(0);

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

    public List<EventDto> getEventsByUserId(Long userId, int from, int size) {
        if (userRepository.findById(userId.intValue()).isEmpty()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        return eventRepository.findByInitiatorId(userId, pageRequest).stream()
                .map(EventMapper::toDto)
                .toList();
    }

    @Transactional
    public EventDto patchEvent(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event does not exist"));
        if (existingEvent.getState().equals(State.PUBLISHED)) {
            log.error("Event is published");
            throw new ExistsException("Event is published");
        }
        if (existingEvent.getInitiator().getId() != userId.intValue()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        if (existingEvent.getState().equals(State.PUBLISH_EVENT)) {
            log.error("State is published");
            throw new BadRequestException("Event is already published");
        }
        return updateEvent(existingEvent, updateEventDto);
    }

    public EventDto getByEventId(Long eventId, String uri) {
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event not found");
            throw new NotFoundException("Event not found");
        }
        Event event = eventRepository.findById(eventId).get();

        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("State is not published");
            throw new NotFoundException("State is not published");
        }
        if (statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody() != null) {
            event.setViews(Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).size());
        } else {
            event.setViews(0);
        }

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toDto(savedEvent);
    }

    public List<EventDto> getOpenEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        Integer from,
                                        Integer size,
                                        String uri) {
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
                    List<Event> events = eventRepository.getOpenAvailableEventsSortsByEventDate(text, categories, paid, rangeStart, rangeEnd, pageRequest);

                    for (Event event : events) {
                        if (!Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).isEmpty()) {
                            event.setViews(Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).size());
                        } else {
                            event.setViews(0);
                        }
                    }


                    List<Event> savedEvents = eventRepository.saveAll(events);

                    return savedEvents.stream()
                            .map(EventMapper::toDto)
                            .toList();
                } else {
                    List<Event> events = eventRepository.getOpenUnAvailableEventsSortsByEventDate(text, categories, paid, rangeStart, rangeEnd, pageRequest);

                    for (Event event : events) {
                        if (!Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).isEmpty()) {
                            event.setViews(Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).size());
                        } else {
                            event.setViews(0);
                        }
                    }


                    List<Event> savedEvents = eventRepository.saveAll(events);

                    return savedEvents.stream()
                            .map(EventMapper::toDto)
                            .toList();
                }
            case "VIEWS":
                if (onlyAvailable) {
                    List<Event> events = eventRepository.getOpenAvailableEventsSortsByViews(text, categories, paid, rangeStart, rangeEnd, pageRequest);

                    for (Event event : events) {
                        if (!Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).isEmpty()) {
                            event.setViews(Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).size());
                        } else {
                            event.setViews(0);
                        }
                    }


                    List<Event> savedEvents = eventRepository.saveAll(events);

                    return savedEvents.stream()
                            .map(EventMapper::toDto)
                            .toList();
                } else {
                    List<Event> events = eventRepository.getOpenUnAvailableEventsSortsByViews(text, categories, paid, rangeStart, rangeEnd, pageRequest);

                    for (Event event : events) {
                        if (!Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).isEmpty()) {
                            event.setViews(Objects.requireNonNull(statsClient.getStat(event.getCreatedOn(), LocalDateTime.now(), List.of(uri), true).getBody()).size());
                        } else {
                            event.setViews(0);
                        }
                    }


                    List<Event> savedEvents = eventRepository.saveAll(events);

                    return savedEvents.stream()
                            .map(EventMapper::toDto)
                            .toList();
                }
            default:
                log.error("Invalid sort value");
                throw new BadRequestException("Invalid sort value");
        }
    }

    public List<EventDto> getEvents(List<Long> usersIds,
                                    List<String> states,
                                    List<Long> categoriesIds,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Integer from,
                                    Integer size) {

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(1000);
        }

        if (rangeEnd.isBefore(rangeStart)) {
            log.error("rangeEnd is before rangeStart");
            throw new BadRequestException("rangeEnd is before rangeStart");
        }

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<State> state;
        if (states != null) {
            state = states.stream().map(s -> State.valueOf(s.toUpperCase())).toList();
        } else {
            state = null;
        }
        List<Event> events = eventRepository.getAllEvents(usersIds, state, categoriesIds, rangeStart, rangeEnd, pageRequest);
        return events.stream()
                .map(EventMapper::toDto)
                .toList();
    }

    @Transactional
    public EventDto patchEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event does not exist"));
        return updateEvent(existingEvent, updateEventDto);
    }


    private EventDto updateEvent(Event existingEvent, UpdateEventDto updateEventDto) {
        boolean needsUpdate = false;
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
            if (!existingEvent.getCategory().equals(categoryRepository.findById(updateEventDto.getCategory().intValue()).get())) {
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
                locationRepository.save(updateEventDto.getLocation());
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
            if (updateEventDto.getParticipantLimit() < 0) {
                log.error("ParticipantLimit can't be less than 0");
                throw new BadRequestException("ParticipantLimit can't be less than 0");
            }
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
                if (updateEventDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                    if (existingEvent.getState().equals(State.REJECTED) || existingEvent.getState().equals(State.PUBLISHED)) {
                        log.error("State can't be published");
                        throw new ExistsException("State can't be published");
                    }
                    existingEvent.setState(State.PUBLISHED);
                } else if (updateEventDto.getStateAction().equals(State.REJECT_EVENT)) {
                    if (existingEvent.getState().equals(State.CANCELED) || existingEvent.getState().equals(State.PUBLISHED)) {
                        log.error("State can't be published");
                        throw new ExistsException("State can't be published");
                    }
                    existingEvent.setState(State.REJECTED);
                } else if (updateEventDto.getStateAction().equals(State.SEND_TO_REVIEW)) {
                    existingEvent.setState(State.PENDING);
                } else if (updateEventDto.getStateAction().equals(State.CANCEL_REVIEW)) {
                    existingEvent.setState(State.CANCELED);
                }
                needsUpdate = true;
            }
        }

        if (updateEventDto.getTitle() != null) {
            if (!existingEvent.getTitle().equals(updateEventDto.getTitle())) {
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
