package ru.practicum.explorewithme.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.dtos.CommentDto;
import ru.practicum.explorewithme.main.models.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto mapToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                UserMapper.toUserShortDto(comment.getUser()),
                EventMapper.toEventShortDto((comment.getEvent())),
                comment.getCreated_on()
        );
    }
}
