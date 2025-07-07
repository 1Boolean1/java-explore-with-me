package ru.practicum.explorewithme.dto.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime time;
}
