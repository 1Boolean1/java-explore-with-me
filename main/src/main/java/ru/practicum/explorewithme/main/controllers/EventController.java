package ru.practicum.explorewithme.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.gateway.client.StatsClient;
import ru.practicum.explorewithme.main.dtos.EventCreateDto;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.dtos.UpdateEventDto;
import ru.practicum.explorewithme.main.models.State;
import ru.practicum.explorewithme.main.services.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class EventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    public EventController(final EventService eventService, StatsClient statsClient) {
        this.eventService = eventService;
        this.statsClient = statsClient;
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getUserEvents(@PathVariable final Long userId, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("getUserEvents userId = {}", userId);
        return eventService.getEventsByUserId(userId);
    }

    @PostMapping("/users/{userId}/events")
    public EventDto createUserEvent(@PathVariable final Long userId, @RequestBody final EventCreateDto eventDto, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("createUserEvent userId = {}", userId);
        return eventService.save(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getUserEvent(@PathVariable final Long userId, @PathVariable final Long eventId, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("getUserEvent userId = {}, eventId = {}", userId, eventId);
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEvent(@PathVariable final Long userId, @PathVariable final Long eventId, @RequestBody UpdateEventDto updateEventDto, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("updateEvent userId = {}, eventId = {}", userId, eventId);
        return eventService.patchEvent(userId, eventId, updateEventDto);
    }

    @GetMapping("/events/{id}")
    public EventDto getEvent(@PathVariable final Long id, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("getEvent userId = {}", id);
        return eventService.getByEventId(id);
    }

    @GetMapping("/events")
    public List<EventDto> getOpenEvents(@RequestParam(required = false, defaultValue = "") String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Get events");
        return eventService.getOpenEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    //написать дефолт значения для состояний
    @GetMapping("/admin/events")
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> usersIds,
                                    @RequestParam(required = false) List<State> states,
                                    @RequestParam(required = false) List<Long> categoriesIds,
                                    @RequestParam(required = false) LocalDateTime rangeStart,
                                    @RequestParam(required = false) LocalDateTime rangeEnd,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("Get events");
        return eventService.getEvents(usersIds, states, categoriesIds, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable final Long eventId,
                                       @RequestBody UpdateEventDto updateEventDto,
                                       HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        log.info("updateEvent userId = {}", eventId);
        return eventService.patchEventByAdmin(eventId, updateEventDto);
    }

}
