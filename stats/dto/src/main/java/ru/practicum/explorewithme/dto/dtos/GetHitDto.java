package ru.practicum.explorewithme.dto.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHitDto {
    private String app;
    private String uri;
    private long hits;
}
