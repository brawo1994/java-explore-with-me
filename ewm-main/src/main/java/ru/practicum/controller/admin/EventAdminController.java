package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.model.enums.State;
import ru.practicum.service.event.EventService;
import ru.practicum.util.Pagination;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static ru.practicum.util.Formatter.DATE_TIME_FORMATTER_PATTERN;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByFilters(@RequestParam(name = "users", required = false) List<Long> users,
                                                 @RequestParam(required = false) List<State> states,
                                                 @RequestParam(name = "categories", required = false)
                                                     List<Long> categories,
                                                 @RequestParam(name = "rangeStart", required = false)
                                                     @DateTimeFormat(pattern = DATE_TIME_FORMATTER_PATTERN)
                                                     LocalDateTime rangeStart,
                                                 @RequestParam(name = "rangeEnd", required = false)
                                                     @DateTimeFormat(pattern = DATE_TIME_FORMATTER_PATTERN)
                                                     LocalDateTime rangeEnd,
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Received GET request /admin/events with params: users {}, states {}, categories {}, rangeStart " +
                "{}, rangeEnd {}, from {}, size {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAdminEventsByFilters(users, states, categories, rangeStart, rangeEnd,
                new Pagination(from, size, Sort.by(ASC,"id")));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByIdAndInitiatorId(@PathVariable("eventId") Long eventId,
                                                      @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("Received PATCH request /admin/events/{} with body: {}", eventId, updateEventDto);
        return eventService.updateEventByAdmin(eventId, updateEventDto);
    }
}
