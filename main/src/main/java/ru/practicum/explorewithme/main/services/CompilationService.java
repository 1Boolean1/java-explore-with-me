package ru.practicum.explorewithme.main.services;

import lombok.extern.slf4j.Slf4j;
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
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setTitle(addCompilationDto.getTitle());
        compilationDto.setPinned(addCompilationDto.getPinned());
        compilationDto.setEvents(eventRepository.findAllById(addCompilationDto.getEventsIds()));
        compilationRepository.save(CompilationMapper.mapToCompilation(compilationDto));
        return compilationDto;
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
        if (!updateCompilationDto.getEventsIds().isEmpty()) {
            List<Long> eventsIds = existingCompilation.getEvents().stream().map(Event::getId).toList();
            if (!eventsIds.equals(updateCompilationDto.getEventsIds())) {
                log.info("Updating existing events");
                List<Event> events = eventRepository.findAllById(updateCompilationDto.getEventsIds());
                existingCompilation.setEvents(events);
            }
        }

        if (needsUpdate) {
            Compilation updatedCompilation = compilationRepository.save(existingCompilation);
            return CompilationMapper.mapToCompilationDto(updatedCompilation);
        } else {
            return CompilationMapper.mapToCompilationDto(existingCompilation);
        }
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        if (from < compilationRepository.findAll().size()) {
            log.error("From must be less than or equal to compilations count");
            throw new BadRequestException("From must be less than or equal to compilations count");
        }
        return compilationRepository.findByPinned(pinned, from, size);
    }

    public CompilationDto getCompilation(Long id) {
        if (compilationRepository.findById(id.intValue()).isPresent()) {
            log.error("Compilation not found");
            throw new NotFoundException("Compilation not found");
        }
        return CompilationMapper.mapToCompilationDto(compilationRepository.findById(id.intValue()).get());
    }

}

