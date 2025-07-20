package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.models.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long id, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR e.category.id IN ?2) " +
            "AND (?3 IS NULL OR e.paid = ?3) " +
            "AND e.eventDate BETWEEN ?4 AND ?5 " +
            "AND e.confirmedRequests < e.participantLimit " +
            "AND e.state = ru.practicum.explorewithme.main.models.State.PUBLISHED " +
            "ORDER BY e.views ")
    List<Event> getOpenAvailableEventsSortsByViews(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR e.category.id IN ?2) " +
            "AND (?3 IS NULL OR e.paid = ?3) " +
            "AND e.eventDate BETWEEN ?4 AND ?5 " +
            "AND e.confirmedRequests < e.participantLimit " +
            "AND e.state = ru.practicum.explorewithme.main.models.State.PUBLISHED " +
            "ORDER BY e.eventDate ")
    List<Event> getOpenAvailableEventsSortsByEventDate(String text,
                                                       List<Long> categories,
                                                       Boolean paid,
                                                       LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd,
                                                       Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR e.category.id IN ?2) " +
            "AND (?3 IS NULL OR e.paid = ?3) " +
            "AND e.eventDate BETWEEN ?4 AND ?5 " +
            "AND e.state = ru.practicum.explorewithme.main.models.State.PUBLISHED " +
            "ORDER BY e.views ")
    List<Event> getOpenUnAvailableEventsSortsByViews(String text,
                                                     List<Long> categories,
                                                     Boolean paid,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR e.category.id IN ?2) " +
            "AND (?3 IS NULL OR e.paid = ?3) " +
            "AND e.eventDate BETWEEN ?4 AND ?5 " +
            "AND e.state = ru.practicum.explorewithme.main.models.State.PUBLISHED " +
            "ORDER BY e.eventDate ")
    List<Event> getOpenUnAvailableEventsSortsByEventDate(String text,
                                                         List<Long> categories,
                                                         Boolean paid,
                                                         LocalDateTime rangeStart,
                                                         LocalDateTime rangeEnd,
                                                         Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (?1 IS NULL OR e.initiator.id IN ?1) " +
            "AND (?2 IS NULL OR e.state IN ?2) " +
            "AND (?3 IS NULL OR e.category.id IN ?3) " +
            "AND e.eventDate BETWEEN ?4 AND ?5 " +
            "ORDER BY e.id ASC")
    List<Event> getAllEvents(List<Long> usersIds,
                             List<State> states,
                             List<Long> categoriesIds,
                             LocalDateTime rangeStart,
                             LocalDateTime rangeEnd,
                             Pageable pageable);

}
