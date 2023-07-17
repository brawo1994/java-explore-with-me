package ru.practicum.service.comment.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.exeption.ConstraintViolationException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.mapper.comment.CommentMapper;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;
import ru.practicum.repository.comment.CommentRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.user.UserRepository;
import ru.practicum.service.comment.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentFullDto createComment(Long userId, NewCommentDto newCommentDto) {
        User user = getUserIfExistOrThrow(userId);
        Event event = getEventIfExistOrThrow(newCommentDto.getEventId());
        Comment comment = commentRepository.save(CommentMapper.toModel(newCommentDto, user, event));
        log.info("Comment with id: {} added to DB", comment.getId());
        return CommentMapper.toFullDto(comment);
    }

    @Override
    public List<CommentFullDto> getCommentsByAuthorId(Long userId, Pageable pageable) {
        getUserIfExistOrThrow(userId);
        return commentRepository.findAllByAuthorId(userId, pageable).stream()
                .map(CommentMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto updateCommentByOwner(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = getCommentIfExistAndUserIdIsOwnerOrThrow(commentId, userId);
        return updateComment(comment, updateCommentDto);
    }

    @Override
    public void deleteCommentByOwner(Long userId, Long commentId) {
        getCommentIfExistAndUserIdIsOwnerOrThrow(commentId, userId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id: {} deleted from DB", commentId);
    }

    @Override
    public CommentFullDto updateCommentByAdmin(Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = getCommentIfExistOrThrow(commentId);
        return updateComment(comment, updateCommentDto);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        getCommentIfExistOrThrow(commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id: {} deleted from DB", commentId);
    }

    @Override
    public CommentFullDto getCommentById(Long commentId) {
        return CommentMapper.toFullDto(getCommentIfExistOrThrow(commentId));
    }

    @Override
    public List<CommentFullDto> getCommentsByEventId(Long eventId, Pageable pageable) {
        getEventIfExistOrThrow(eventId);
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(CommentMapper::toFullDto)
                .collect(Collectors.toList());
    }

    private User getUserIfExistOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден в системе"));
    }

    private Event getEventIfExistOrThrow(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId +
                " не найдено в системе"));
    }

    private Comment getCommentIfExistOrThrow(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий с id " +
                commentId + " не найден в системе"));
    }

    private Comment getCommentIfExistAndUserIdIsOwnerOrThrow(long commentId, long userId) {
        Comment comment = getCommentIfExistOrThrow(commentId);
        if (comment.getAuthor() != getUserIfExistOrThrow(userId)) {
            throw new ConstraintViolationException("Пользователь с id " + userId + "не является автором " +
                    "комментария с id " + commentId);
        }
        return comment;
    }

    private CommentFullDto updateComment(Comment currentComment, UpdateCommentDto newUpdateCommentDto) {
        currentComment.setText(newUpdateCommentDto.getText());
        Comment updatedComment = commentRepository.save(currentComment);
        log.info("Comment with id: {} updated in DB", currentComment.getId());
        return CommentMapper.toFullDto(updatedComment);
    }
}
