package ru.practicum.explorewithme.main.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.AddCompilationDto;
import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.dtos.UpdateCompilationDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.CompilationMapper;
import ru.practicum.explorewithme.main.models.Compilation;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.repositories.CompilationRepository;
import ru.practicum.explorewithme.main.repositories.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationService(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public CompilationDto addCompilation(final AddCompilationDto addCompilationDto) {
        if (addCompilationDto.getTitle().isBlank()) {
            log.error("Title is blank");
            throw new BadRequestException("Title is blank");
        }
        Compilation compilation = new Compilation();
        compilation.setTitle(addCompilationDto.getTitle());
        compilation.setPinned(addCompilationDto.getPinned());
        List<Event> events = new ArrayList<>();
        for (Long id : addCompilationDto.getEvents()) {
            events.add(eventRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("No event found with id: " + id)
            ));
        }
        compilation.setEvents(events);
        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }

    public void deleteCompilation(final Long id) {
        if (!compilationRepository.existsById(id.intValue())) {
            log.error("Compilation not found");
            throw new NotFoundException("Compilation not found");
        }

        compilationRepository.deleteById(id.intValue());
    }

    public CompilationDto updateCompilation(Long compilationId,
                                            UpdateCompilationDto updateCompilationDto) {
        Compilation existingCompilation = compilationRepository.findById(compilationId.intValue())
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        boolean needsUpdate = false;

        if (updateCompilationDto.getTitle() != null && !updateCompilationDto.getTitle().isBlank()) {
            if (!existingCompilation.getTitle().equals(updateCompilationDto.getTitle())) {
                log.info("Updating existing title");
                existingCompilation.setTitle(updateCompilationDto.getTitle());
                needsUpdate = true;
            }
        }
        if (updateCompilationDto.getPinned() != null) {
            if (!existingCompilation.getPinned().equals(updateCompilationDto.getPinned())) {
                log.info("Updating existing pinned");
                existingCompilation.setPinned(updateCompilationDto.getPinned());
            }
            needsUpdate = true;
        }
        if (updateCompilationDto.getEventsIds() != null) {
            if (!updateCompilationDto.getEventsIds().isEmpty()) {
                List<Long> eventsIds = existingCompilation.getEvents().stream().map(Event::getId).toList();
                if (!eventsIds.equals(updateCompilationDto.getEventsIds())) {
                    log.info("Updating existing events");
                    List<Event> events = eventRepository.findAllById(updateCompilationDto.getEventsIds());
                    existingCompilation.setEvents(events);
                }
            }
        }

        if (needsUpdate) {
            Compilation updatedCompilation = compilationRepository.save(existingCompilation);
            return CompilationMapper.mapToCompilationDto(updatedCompilation);
        } else {
            return CompilationMapper.mapToCompilationDto(existingCompilation);
        }
    }

    public List<CompilationDto> getCompilations(boolean pinned, Integer from, Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return compilationRepository.findByPinned(pinned, pageRequest).stream().map(CompilationMapper::mapToCompilationDto).toList();
    }

    public CompilationDto getCompilation(Long id) {
        Compilation compilation = compilationRepository.findById(id.intValue()).orElseThrow(
                () -> new NotFoundException("Compilation with id=" + id + " not found")
        );
        return CompilationMapper.mapToCompilationDto(compilation);
    }

}

