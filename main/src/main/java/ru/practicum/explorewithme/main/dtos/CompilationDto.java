package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    @Min(1)
    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    @NotBlank
    private String title;
}
