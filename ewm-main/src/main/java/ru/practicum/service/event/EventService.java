package ru.practicum.service.event;

import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateRequestDto;
import ru.practicum.dto.request.RequestUpdateResponseDto;
import ru.practicum.model.enums.EventSort;
import ru.practicum.model.enums.State;
import ru.practicum.util.Pagination;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, EventDto eventDto);

    List<EventShortDto> getEventsByInitiatorId(Long userId, Pagination pagination);

    EventFullDto getEventByIdAndInitiatorId(Long userId, Long eventId);

    EventFullDto updateEventByIdAndInitiatorId(Long userId, Long eventId, UpdateEventDto updateEventDto);

    List<EventFullDto> getAdminEventsByFilters(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Pagination pagination);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto);

    List<RequestDto> getRequestsByEvent(Long userId, Long eventId);

    RequestUpdateResponseDto changeRequestsState(Long userId, Long eventId,
                                                 RequestUpdateRequestDto requestUpdateRequestDto);

    List<EventShortDto> getEventsByFilters(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                           Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
