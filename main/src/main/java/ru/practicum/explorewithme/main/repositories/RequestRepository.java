package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.main.models.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    int countByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);
}
