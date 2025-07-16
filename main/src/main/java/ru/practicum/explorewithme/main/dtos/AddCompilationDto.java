package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCompilationDto {

    private List<Long> events = new ArrayList<>();

    private Boolean pinned = Boolean.FALSE;

    @NotBlank
    @NotNull
    @Size(max = 50)
    private String title;
}
