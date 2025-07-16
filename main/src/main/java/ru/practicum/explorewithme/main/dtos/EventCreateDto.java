package ru.practicum.explorewithme.main.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explorewithme.main.models.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventCreateDto {
    @NotBlank
    @NotNull
    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlank
    @NotNull
    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid = false;

    private int participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @NotNull
    @Length(min = 3, max = 120)
    private String title;
}
