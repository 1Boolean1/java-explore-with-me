package ru.practicum.explorewithme.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.main.models.RequestsStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchStatusRequestDto {
    private List<Long> requestIds;

    private RequestsStatus status;
}
