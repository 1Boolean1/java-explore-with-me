package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.dtos.EventDto;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.models.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT new ru.practicum.explorewithme.main.dtos.EventDto(e.id, e.annotation, e.category, e.confirmedRequests, e.createdOn, e.description, e.eventDate, e.initiator, e.location, e.paid, e.participantLimit, e.publishedOn, e.requestModeration, e.state, e.title, e.views) " +
            "FROM Event e " +
            "WHERE e.initiator.id = ?1")
    List<EventDto> findEventDtoByInitiatorId(Long id);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND e.category.id IN ?2 " +
            "AND e.paid = ?3" +
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
            "AND e.category.id IN ?2 " +
            "AND e.paid = ?3" +
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
            "AND e.category.id IN ?2 " +
            "AND e.paid = ?3" +
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
            "AND e.category.id IN ?2 " +
            "AND e.paid = ?3" +
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
            "WHERE e.initiator.id IN ?1 " +
            "AND e.state IN ?2 " +
            "AND e.category.id IN ?3 " +
            "AND e.eventDate BETWEEN ?4 AND ?5 " +
            "ORDER BY e.eventDate ")
    List<Event> getAllEvents(List<Long> usersIds,
                                List<State> states,
                                List<Long> categoriesIds,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Pageable pageable);
}
