package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.dtos.CompilationDto;
import ru.practicum.explorewithme.main.models.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    @Query("SELECT new ru.practicum.explorewithme.main.dtos.CompilationDto(comp.id, comp.events, comp.pinned, comp.title) " +
            "FROM Compilation comp " +
            "WHERE comp.pinned = ?1 " +
            "ORDER BY comp.id " +
            "LIMIT ?3 offset ?2")
    public List<CompilationDto> findByPinned(boolean pinned, Integer from, Integer size);
}
