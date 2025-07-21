package ru.practicum.explorewithme.main.controllers;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dtos.CommentCreateDto;
import ru.practicum.explorewithme.main.dtos.CommentDto;
import ru.practicum.explorewithme.main.services.CommentService;

import java.util.List;

@RestController
@Slf4j
@Validated
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //private
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public CommentDto commentPost(@PathVariable @Positive long userId,
                                  @PathVariable @Positive long eventId,
                                  @RequestBody CommentCreateDto commentCreateDto) {
        log.info("comment post");
        return commentService.addComment(userId, eventId, commentCreateDto);
    }

    //public
    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getComments(@PathVariable @Positive long eventId) {
        log.info("get comments");
        return commentService.getComments(eventId);
    }

    //public
    @GetMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto getComment(@PathVariable @Positive long eventId,
                                 @PathVariable @Positive long commentId) {
        log.info("get comment");
        return commentService.getComment(eventId, commentId);
    }

    //admin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/comments/{commentId}")
    public void deleteComment(@PathVariable @Positive long commentId) {
        log.info("delete comment");
        commentService.deleteComment(commentId);
    }

    //private
    @PatchMapping("/users/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto patchComment(@PathVariable @Positive long userId,
                                   @PathVariable @Positive long eventId,
                                   @PathVariable @Positive long commentId,
                                   @RequestBody CommentCreateDto commentCreateDto) {
        log.info("patch comment");
        return commentService.patchComment(userId, eventId, commentId, commentCreateDto);
    }
}
