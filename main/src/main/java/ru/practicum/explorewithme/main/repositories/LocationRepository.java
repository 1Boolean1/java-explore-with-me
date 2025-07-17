package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.main.models.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
