package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompilationDto {

    private List<Long> eventsIds;

    private Boolean pinned = Boolean.FALSE;

    @Size(max = 50)
    private String title;
}