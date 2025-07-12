package ru.practicum.explorewithme.main.mappers;

import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.models.Compilation;

public class CompilationMapper {
    public static Compilation mapToCompilation(final CompilationDto compilationDto) {
        return new Compilation(
                compilationDto.getId(),
                compilationDto.getEvents(),
                compilationDto.getPinned(),
                compilationDto.getTitle()
        );
    }

    public static CompilationDto mapToCompilationDto(final Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
