package ru.practicum.explorewithme.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestsResultsDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
