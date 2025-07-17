package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.PatchStatusRequestDto;
import ru.practicum.explorewithme.main.dtos.RequestDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.ExistsException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.RequestMapper;
import ru.practicum.explorewithme.main.models.*;
import ru.practicum.explorewithme.main.repositories.EventRepository;
import ru.practicum.explorewithme.main.repositories.RequestRepository;
import ru.practicum.explorewithme.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RequestService(RequestRepository requestRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
        User user = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ExistsException("You have already submitted a request for this event.");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ExistsException("The initiator cannot submit a request for their own event.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ExistsException("Cannot submit a request for an unpublished event.");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ExistsException("The participant limit for the event has been reached.");
        }

        Request newRequest = new Request();
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setEvent(event);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestsStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            newRequest.setStatus(RequestsStatus.PENDING);
        }

        Request savedRequest = requestRepository.save(newRequest);
        return RequestMapper.toDto(savedRequest);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        if (requestRepository.findById(requestId.intValue()).isEmpty()) {
            log.error("Request with id {} not found", requestId);
            throw new NotFoundException("Request not found");
        }

        if (userRepository.findById(userId.intValue()).isEmpty()) {
            log.error("User with id {} not found", userId);
            throw new NotFoundException("User not found");
        }
        Request oldRequest = requestRepository.findById(requestId.intValue()).get();
        Request request = new Request(oldRequest.getId(), oldRequest.getCreated(), oldRequest.getRequester(), oldRequest.getEvent(), RequestsStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    public List<RequestDto> getRequests(Long userId) {
        if (userRepository.findById(userId.intValue()).isEmpty()) {
            log.error("User with id {} not found", userId);
            throw new NotFoundException("User not found");
        }

        return requestRepository.findAllByRequesterId(userId).stream().map(RequestMapper::toDto).toList();
    }

    public List<RequestDto> getUserRequests(Long userId, Long eventId) {
        if (userRepository.findById(userId.intValue()).isEmpty()) {
            log.error("User with id {} not found", userId);
            throw new NotFoundException("User not found");
        }

        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event with id {} not found", eventId);
            throw new NotFoundException("Event not found");
        }

        if (!eventRepository.findById(eventId).get().getInitiator().getId().equals(userId)) {
            log.error("User with id {} isn't initiator", userId);
            throw new ExistsException("User with id {} isn't initiator");
        }

        return requestRepository.findAllByEventId(eventId).stream().map(RequestMapper::toDto).toList();
    }

    @Transactional
    public Map<String, List<RequestDto>> patchStatus(Long userId, Long eventId, PatchStatusRequestDto patchDto) {
        if (!userRepository.existsById(userId.intValue())) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("User " + userId + " is not the initiator of event " + eventId);
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return Map.of("confirmedRequest", List.of(), "rejectedRequest", List.of());
        }

        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ExistsException("The participant limit for the event has been reached.");
        }


        List<Request> requestsToUpdate = requestRepository.findAllById(patchDto.getRequestIds().stream().map(Long::intValue).toList());

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();


        for (Request request : requestsToUpdate) {
            if (!request.getStatus().equals(RequestsStatus.PENDING)) {
                throw new ExistsException("Request ID " + request.getId() + " is not in PENDING state.");
            }

            if (patchDto.getStatus().equals(RequestsStatus.CONFIRMED)) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(RequestsStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    requestRepository.save(request);
                    eventRepository.save(event);
                    confirmedRequests.add(RequestMapper.toDto(request));
                } else {
                    request.setStatus(RequestsStatus.REJECTED);
                    requestRepository.save(request);
                    rejectedRequests.add(RequestMapper.toDto(request));
                }
            } else if (patchDto.getStatus().equals(RequestsStatus.REJECTED)) {
                request.setStatus(RequestsStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(RequestMapper.toDto(request));
            }
        }

        return Map.of("confirmedRequest", confirmedRequests, "rejectedRequest", rejectedRequests);
    }
}
