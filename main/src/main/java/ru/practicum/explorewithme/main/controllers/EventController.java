package ru.practicum.explorewithme.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.gateway.client.StatsClient;
import ru.practicum.explorewithme.main.dtos.EventCreateDto;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.dtos.UpdateEventDto;
import ru.practicum.explorewithme.main.services.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
public class EventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    public EventController(final EventService eventService, StatsClient statsClient) {
        this.eventService = eventService;
        this.statsClient = statsClient;
    }


    @GetMapping("/users/{userId}/events")
    public List<EventDto> getUserEvents(@PathVariable @Positive final Long userId,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("getUserEvents userId = {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createUserEvent(@PathVariable @Positive final Long userId,
                                    @RequestBody @Valid final EventCreateDto eventDto,
                                    HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsClient.saveHit(hitDto);
        log.info("createUserEvent userId = {}", userId);
        return eventService.save(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getUserEvent(@PathVariable @Positive final Long userId,
                                 @PathVariable @Positive final Long eventId,
                                 HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsClient.saveHit(hitDto);
        log.info("getUserEvent userId = {}, eventId = {}", userId, eventId);
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEvent(@PathVariable @Positive final Long userId,
                                @PathVariable @Positive final Long eventId,
                                @RequestBody @Valid UpdateEventDto updateEventDto,
                                HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsClient.saveHit(hitDto);
        log.info("updateEvent userId = {}, eventId = {}", userId, eventId);
        return eventService.patchEvent(userId, eventId, updateEventDto);
    }

    @GetMapping("/events/{id}")
    public EventDto getEvent(@PathVariable @Positive final Long id,
                             HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsClient.saveHit(hitDto);
        log.info("getEvent userId = {}", id);
        return eventService.getByEventId(id, request.getRequestURI());
    }

    @GetMapping("/events")
    public List<EventDto> getOpenEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        HitDto hit = new HitDto();
        hit.setApp("explorewithme");
        hit.setIp(request.getRemoteAddr());
        hit.setUri(request.getRequestURI());
        hit.setTime(LocalDateTime.now());
        statsClient.saveHit(hit);
        log.info("Get events");
        return eventService.getOpenEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRequestURI());
    }

    @GetMapping("/admin/events")
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) List<String> states,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get events");
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable @Positive final Long eventId,
                                       @RequestBody @Valid UpdateEventDto updateEventDto,
                                       HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsClient.saveHit(hitDto);
        log.info("updateEvent userId = {}", eventId);
        return eventService.patchEventByAdmin(eventId, updateEventDto);
    }

}
