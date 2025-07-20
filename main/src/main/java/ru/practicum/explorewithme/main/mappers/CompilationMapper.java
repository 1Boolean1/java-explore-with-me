package ru.practicum.explorewithme.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.models.Compilation;

@UtilityClass
public class CompilationMapper {
    public static CompilationDto mapToCompilationDto(final Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents().stream().map(EventMapper::toEventShortDto).toList(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
