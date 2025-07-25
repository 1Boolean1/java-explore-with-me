package ru.practicum.explorewithme.main.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dtos.PatchStatusRequestDto;
import ru.practicum.explorewithme.main.dtos.RequestDto;
import ru.practicum.explorewithme.main.dtos.RequestsResultsDto;
import ru.practicum.explorewithme.main.services.RequestService;

import java.util.List;

@RestController
@Slf4j
@Validated
public class RequestController {
    private final RequestService requestService;


    public RequestController(final RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable @Positive Long userId,
                                    @RequestParam @Positive Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getRequests(@PathVariable @Positive Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getUserRequests(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId) {
        return requestService.getUserRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestsResultsDto patchStatus(@PathVariable @Positive Long userId,
                                          @PathVariable @Positive Long eventId,
                                          @RequestBody @Valid PatchStatusRequestDto patchStatusRequestDto) {
        System.out.println(patchStatusRequestDto);
        return requestService.patchStatus(userId, eventId, patchStatusRequestDto);
    }
}
