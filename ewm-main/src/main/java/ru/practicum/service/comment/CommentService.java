package ru.practicum.service.comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.dto.comment.CommentFullDto;

import java.util.List;

public interface CommentService {
    CommentFullDto createComment(Long userId, NewCommentDto newCommentDto);

    List<CommentFullDto> getCommentsByAuthorId(Long userId, Pageable pageable);

    CommentFullDto updateCommentByOwner(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentByOwner(Long userId, Long commentId);

    CommentFullDto updateCommentByAdmin(Long commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentByAdmin(Long commentId);

    CommentFullDto getCommentById(Long commentId);

    List<CommentFullDto> getCommentsByEventId(Long eventId, Pageable pageable);
}
