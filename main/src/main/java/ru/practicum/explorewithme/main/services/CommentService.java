package ru.practicum.explorewithme.main.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.CommentCreateDto;
import ru.practicum.explorewithme.main.dtos.CommentDto;
import ru.practicum.explorewithme.main.exceptions.ExistsException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.CommentMapper;
import ru.practicum.explorewithme.main.models.Comment;
import ru.practicum.explorewithme.main.models.Event;
import ru.practicum.explorewithme.main.models.User;
import ru.practicum.explorewithme.main.repositories.EventRepository;
import ru.practicum.explorewithme.main.repositories.UserRepository;
import ru.practicum.explorewithme.main.repositories.CommentRepository;

import java.util.List;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public CommentDto addComment(Long userId, long eventId, CommentCreateDto commentCreateDto) {
        if(commentCreateDto.getText().isBlank()){
            log.error("Comment text is blank");
            throw new ExistsException("Comment text is blank");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found")
        );
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(commentCreateDto.getText());
        return CommentMapper.mapToDto(commentRepository.save(comment));
    }

    public List<CommentDto> getComments(long eventId) {
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event not found");
            throw new NotFoundException("Event not found");
        }
        return commentRepository.findByEventId(eventId)
                .stream()
                .map(CommentMapper::mapToDto)
                .toList();
    }

    public CommentDto getComment(long eventId, long commentId) {
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event not found");
            throw new NotFoundException("Event not found");
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not found")
        );
        if (!comment.getEvent().getId().equals(eventId)) {
            log.error("Comment is not the same");
            throw new ExistsException("Comment is not the same");
        }
        return CommentMapper.mapToDto(comment);
    }

    public void deleteComment(long commentId) {
        if (commentRepository.findById(commentId).isEmpty()) {
            log.error("Comment not found");
            throw new NotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    public CommentDto patchComment(long userId, long eventId, long commentId, CommentCreateDto commentCreateDto) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        if (eventRepository.findById(eventId).isEmpty()) {
            log.error("Event not found");
            throw new NotFoundException("Event not found");
        }
        Comment existsComment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not found")
        );
        if(!existsComment.getUser().getId().equals(userId)){
            log.error("Comment is not the same");
            throw new ExistsException("Comment is not the same");
        }

        if (commentCreateDto.getText() != null
                && !commentCreateDto.getText().isBlank()
                && !commentCreateDto.getText().equals(existsComment.getText())) {
            existsComment.setText(commentCreateDto.getText());
        }
        return CommentMapper.mapToDto(commentRepository.save(existsComment));
    }
}
