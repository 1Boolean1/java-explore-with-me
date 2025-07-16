package ru.practicum.explorewithme.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.dtos.HitDto;
import ru.practicum.explorewithme.main.dtos.EventCreateDto;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.dtos.UpdateEventDto;
import ru.practicum.explorewithme.main.services.EventService;
import ru.practicum.explorewithme.server.controllers.StatsController;
import ru.practicum.explorewithme.server.mappers.HitMapper;
import ru.practicum.explorewithme.server.models.Hit;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class EventController {
    private final EventService eventService;
    private final StatsController statsController;

    public EventController(final EventService eventService, StatsController statsController) {
        this.eventService = eventService;
        this.statsController = statsController;
    }


    @GetMapping("/users/{userId}/events")
    public List<EventDto> getUserEvents(@PathVariable final Long userId,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("getUserEvents userId = {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createUserEvent(@PathVariable final Long userId,
                                    @RequestBody @Valid final EventCreateDto eventDto,
                                    HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsController.saveHit(hitDto);
        log.info("createUserEvent userId = {}", userId);
        return eventService.save(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getUserEvent(@PathVariable final Long userId,
                                 @PathVariable final Long eventId,
                                 HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsController.saveHit(hitDto);
        log.info("getUserEvent userId = {}, eventId = {}", userId, eventId);
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEvent(@PathVariable final Long userId,
                                @PathVariable final Long eventId,
                                @RequestBody @Valid UpdateEventDto updateEventDto,
                                HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsController.saveHit(hitDto);
        log.info("updateEvent userId = {}, eventId = {}", userId, eventId);
        return eventService.patchEvent(userId, eventId, updateEventDto);
    }

    @GetMapping("/events/{id}")
    public EventDto getEvent(@PathVariable final Long id,
                             HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsController.saveHit(hitDto);
        log.info("getEvent userId = {}", id);
        return eventService.getByEventId(id, request.getRequestURI());
    }

    @GetMapping("/events")
    public List<EventDto> getOpenEvents(@RequestParam(required = false, defaultValue = "") String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        Hit hit = new Hit();
        hit.setApp("explorewithme");
        hit.setIp(request.getRemoteAddr());
        hit.setUri(request.getRequestURI());
        hit.setTime(LocalDateTime.now());
        statsController.saveHit(HitMapper.mapToDto(hit));
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
    public EventDto updateEventByAdmin(@PathVariable final Long eventId,
                                       @RequestBody @Valid UpdateEventDto updateEventDto,
                                       HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("explorewithme");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTime(LocalDateTime.now());
        statsController.saveHit(hitDto);
        log.info("updateEvent userId = {}", eventId);
        return eventService.patchEventByAdmin(eventId, updateEventDto);
    }

}
