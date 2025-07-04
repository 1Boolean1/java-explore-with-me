package ru.practicum.explorewithme.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.dto.dtos.GetHitDto;
import ru.practicum.explorewithme.server.models.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT new ru.practicum.explorewithme.dto.dtos.GetHitDto(h.app, h.uri, COUNT(DISTINCT h.ip)) FROM Hit h " +
            "WHERE h.time BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<GetHitDto> findUniqByTimestamp(LocalDateTime from, LocalDateTime to);

    @Query("SELECT new ru.practicum.explorewithme.dto.dtos.GetHitDto(h.app, h.uri, COUNT(h.ip)) FROM Hit h " +
            "WHERE h.time BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<GetHitDto> findByTimestamp(LocalDateTime from, LocalDateTime to);

    @Query("SELECT new ru.practicum.explorewithme.dto.dtos.GetHitDto(h.app, h.uri, COUNT(DISTINCT h.ip)) FROM Hit h " +
            "WHERE h.time BETWEEN ?1 AND ?2 AND h.uri = ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<GetHitDto> findUniqByTimestampAndUris(LocalDateTime from, LocalDateTime to, List<String> uri);

    @Query("SELECT new ru.practicum.explorewithme.dto.dtos.GetHitDto(h.app, h.uri, COUNT(h.ip)) FROM Hit h " +
            "WHERE h.time BETWEEN ?1 AND ?2 AND h.uri = ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<GetHitDto> findByTimestampAndUris(LocalDateTime from, LocalDateTime to, List<String> uri);

}
