package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCompilationDto {

    private List<Long> events;

    @NotNull
    private Boolean pinned = Boolean.FALSE;

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
