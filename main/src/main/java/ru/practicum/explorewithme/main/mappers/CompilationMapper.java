package ru.practicum.explorewithme.main.mappers;

import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.models.Compilation;

public class CompilationMapper {

    public static CompilationDto mapToCompilationDto(final Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents().stream().map(EventMapper::toDto).toList(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
