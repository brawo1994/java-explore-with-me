package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateRequestDto;
import ru.practicum.dto.request.RequestUpdateResponseDto;
import ru.practicum.service.event.EventService;
import ru.practicum.util.Pagination;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @RequestBody @Valid EventDto eventDto) {
        log.info("Received POST request /users/{}/events with body: {}", userId, eventDto.toString());
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsByInitiatorId(@PathVariable("userId") Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request /users/{}/events with params: from {}, size {}", userId, from, size);
        return eventService.getEventsByInitiatorId(userId, new Pagination(from, size, Sort.by(ASC, "id")));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndInitiatorId(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId) {
        log.info("Received GET request /users/{}/events/{}", userId, eventId);
        return eventService.getEventByIdAndInitiatorId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByIdAndInitiatorId(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId,
                                        @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("Received PATCH request /users/{}/events/{} with body: {}", userId, eventId, updateEventDto);
        return eventService.updateEventByIdAndInitiatorId(userId, eventId, updateEventDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId) {
        log.info("Received GET request /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResponseDto changeRequestsState(@PathVariable("userId") Long userId,
                                                        @PathVariable("eventId") Long eventId,
                                                        @RequestBody RequestUpdateRequestDto requestUpdateRequestDto) {
        log.info("Received PATCH request /users/{}/events/{}/requests", userId, eventId);
        return eventService.changeRequestsState(userId, eventId, requestUpdateRequestDto);
    }
}
