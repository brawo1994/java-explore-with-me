package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.service.comment.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentFullDto updateComment(@PathVariable("commentId") Long commentId,
                                        @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        log.info("Received PATCH request /admin/comments/{} with body: {}", commentId, updateCommentDto.toString());
        return commentService.updateCommentByAdmin(commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        log.info("Received DELETE request /admin/comments/{}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
