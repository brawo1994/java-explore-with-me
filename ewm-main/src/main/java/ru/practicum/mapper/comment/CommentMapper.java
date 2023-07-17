package ru.practicum.mapper.comment;

import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.mapper.user.UserMapper;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

public final class CommentMapper {

    private CommentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Comment toModel(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .author(author)
                .event(event)
                .text(newCommentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static Comment toModel(UpdateCommentDto updateCommentDto, User author, Event event) {
        return Comment.builder()
                .author(author)
                .event(event)
                .text(updateCommentDto.getText())
                .build();
    }

    public static CommentFullDto toFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .author(UserMapper.toShortDto(comment.getAuthor()))
                .eventId(comment.getEvent().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
