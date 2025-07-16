package ru.practicum.explorewithme.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.explorewithme.dto.dtos.HitDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatsClient {

    private final WebClient webClient;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(statsServerUrl)
                .build();
    }

    public ResponseEntity<List<HitDto>> getStat(LocalDateTime start,
                                                LocalDateTime end,
                                                List<String> uris,
                                                Boolean unique) {

        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end);
                    if (uris != null)
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    if (unique != null)
                        uriBuilder.queryParam("unique", unique);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntityList(HitDto.class)
                .block();
    }

    public void saveHit(HitDto requestDto) {
        webClient.post()
                .uri("/hit")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

}
