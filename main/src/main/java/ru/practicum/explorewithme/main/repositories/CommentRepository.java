package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.main.models.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(Long eventId);
}
