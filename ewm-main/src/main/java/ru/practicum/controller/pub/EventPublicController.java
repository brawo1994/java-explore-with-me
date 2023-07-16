package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.enums.EventSort;
import ru.practicum.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Formatter.DATE_TIME_FORMATTER_PATTERN;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByFilters(@RequestParam(name = "text", required = false) String text,
                                                  @RequestParam(name = "categories", required = false)
                                                     List<Long> categories,
                                                  @RequestParam(name = "paid", required = false) Boolean paid,
                                                  @RequestParam(name = "rangeStart", required = false)
                                                      @DateTimeFormat(pattern = DATE_TIME_FORMATTER_PATTERN)
                                                      LocalDateTime rangeStart,
                                                  @RequestParam(name = "rangeEnd", required = false)
                                                      @DateTimeFormat(pattern = DATE_TIME_FORMATTER_PATTERN)
                                                      LocalDateTime rangeEnd,
                                                  @RequestParam(name = "onlyAvailable", required = false)
                                                     Boolean onlyAvailable,
                                                  @RequestParam(name = "sort", required = false)
                                                     EventSort sort,
                                                  @RequestParam(name = "from" , defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        log.info("Received GET request /events with params: text {}, categories {}, paid {}, rangeStart " +
                "{}, rangeEnd {}, onlyAvailable {}, sort {}, from {}, size {}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEventsByFilters(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable("eventId") Long eventId, HttpServletRequest request) {
        log.info("Received GET request /events/{}", eventId);
        return eventService.getEventById(eventId, request);
    }
}
