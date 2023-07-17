package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.service.comment.CommentService;
import ru.practicum.util.Pagination;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable("userId") Long userId,
                                        @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Received POST request /users/{}/comments with body: {}", userId, newCommentDto.toString());
        return commentService.createComment(userId, newCommentDto);
    }

    @GetMapping
    public List<CommentFullDto> getCommentsByAuthorId(@PathVariable("userId") Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request /users/{}/comments with params: from {}, size {}", userId, from, size);
        return commentService.getCommentsByAuthorId(userId, new Pagination(from, size, Sort.by(ASC, "id")));
    }

    @PatchMapping("/{commentId}")
    public CommentFullDto updateComment(@PathVariable("userId") Long userId,
                                        @PathVariable("commentId") Long commentId,
                                        @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        log.info("Received PATCH request /users/{}/comments/{} with body: {}", userId, commentId, updateCommentDto);
        return commentService.updateCommentByOwner(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("commentId") Long commentId) {
        log.info("Received DELETE request /users/{}/comments/{}", userId, commentId);
        commentService.deleteCommentByOwner(userId, commentId);
    }
}
