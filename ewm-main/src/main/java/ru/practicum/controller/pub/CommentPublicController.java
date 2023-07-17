package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.service.comment.CommentService;
import ru.practicum.util.Pagination;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getCommentsByEventId(@RequestParam Long eventId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request /comments with params: eventId {}, from {}, size {}", eventId, from, size);
        return commentService.getCommentsByEventId(eventId, new Pagination(from, size, Sort.by(ASC, "id")));
    }

    @GetMapping("/{commentId}")
    public CommentFullDto getComment(@PathVariable("commentId") Long commentId) {
        log.info("Received GET request /comments/{}", commentId);
        return commentService.getCommentById(commentId);
    }
}
