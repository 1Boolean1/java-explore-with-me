package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
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

    @Transactional
    public CompilationDto addCompilation(final AddCompilationDto addCompilationDto) {
        if (addCompilationDto.getTitle().isBlank()) {
            log.error("Title is blank");
            throw new BadRequestException("Title is blank");
        }
        if (addCompilationDto.getEvents() == null) {
            addCompilationDto.setEvents(new ArrayList<>());
        }
        Compilation compilation = new Compilation();
        compilation.setTitle(addCompilationDto.getTitle());
        compilation.setPinned(addCompilationDto.getPinned());
        compilation.setEvents(eventRepository.findAllById(addCompilationDto.getEvents()));
        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    public void deleteCompilation(final Long id) {
        if (!compilationRepository.existsById(id.intValue())) {
            log.error("Compilation not found");
            throw new NotFoundException("Compilation not found");
        }

        compilationRepository.deleteById(id.intValue());
    }

    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(compilationId.intValue())
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compilationId + " not found"));

        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getEvents() != null) {
            if (updateCompilationDto.getEvents().isEmpty()) {
                compilation.setEvents(new ArrayList<>());
            } else {
                List<Event> events = eventRepository.findAllById(updateCompilationDto.getEvents());
                compilation.setEvents(events);
            }
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.mapToCompilationDto(updatedCompilation);
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

