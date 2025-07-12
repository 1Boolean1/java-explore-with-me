package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCompilationDto {

    private List<Long> eventsIds;

    private Boolean pinned = Boolean.FALSE;

    @NotBlank
    @NotNull
    private String title;
}
