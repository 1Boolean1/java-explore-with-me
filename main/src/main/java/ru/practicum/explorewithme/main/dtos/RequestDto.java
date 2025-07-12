package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.models.RequestsStatus;
import ru.practicum.explorewithme.main.models.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {
    @Min(1)
    private Long id;

    private LocalDateTime created;

    private User requesterId;

    private Event event;

    private RequestsStatus status;
}
