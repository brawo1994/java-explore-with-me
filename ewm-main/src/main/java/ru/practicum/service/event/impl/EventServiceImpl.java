package ru.practicum.service.event.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateRequestDto;
import ru.practicum.dto.request.RequestUpdateResponseDto;
import ru.practicum.exeption.BadRequestException;
import ru.practicum.exeption.BadStateException;
import ru.practicum.exeption.ConstraintViolationException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.mapper.event.EventMapper;
import ru.practicum.mapper.event.LocationMapper;
import ru.practicum.mapper.request.RequestMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.enums.EventSort;
import ru.practicum.model.enums.State;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.request.RequestRepository;
import ru.practicum.repository.user.UserRepository;
import ru.practicum.service.event.EventService;
import ru.practicum.util.Pagination;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ewm.main-service.name}")
    private String appName;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, EventDto eventDto) {
        if (eventDto.getPaid() == null) {
            eventDto.setPaid(false);
        }
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(true);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0L);
        }
        LocalDateTime now = LocalDateTime.now();
        validateEventDate(now, eventDto.getEventDate());
        User user = getUserIfExistOrThrow(userId);
        Category category = getCategoryIfExistOrThrow(eventDto.getCategory());
        Event event = eventRepository.save(EventMapper.toModel(eventDto, category, user, now, State.PENDING));
        event.setViews(0L);
        event.setViews(0L);
        log.info("Event with id: {} added to DB", event.getId());
        return EventMapper.toFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByInitiatorId(Long userId, Pagination pagination) {
        getUserIfExistOrThrow(userId);
        List<Event> events = eventRepository.findEventsByInitiatorId(userId, pagination);
        for (Event event : events) {
            event.setConfirmedRequests(getConfirmedRequestsByEvent(event));
        }
        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdAndInitiatorId(Long userId, Long eventId) {
        getUserIfExistOrThrow(userId);
        Event event = getEventIfExistAndUserIdIsOwnerOrThrow(eventId, userId);
        event.setConfirmedRequests(getConfirmedRequestsByEvent(event));
        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByIdAndInitiatorId(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        Event event = getEventIfExistAndUserIdIsOwnerOrThrow(eventId, userId);
        if (updateEventDto.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            validateEventDate(now, updateEventDto.getEventDate());
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (!(event.getState().equals(State.REJECTED) || event.getState().equals(State.PENDING))) {
            throw new BadStateException("Only pending or canceled events can be changed");
        }
        if (updateEventDto.getStateAction() != null) {
            switch (updateEventDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new BadStateException("Invalid StateAction value");
            }
        }
        return EventMapper.toFullDto(eventRepository.save(updateEvent(event, updateEventDto)));
    }

    @Override
    public List<EventFullDto> getAdminEventsByFilters(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Pagination pagination) {
        List<Event> events = eventRepository.findEventsByFilters(userIds, states, categoryIds, rangeStart, rangeEnd,
                pagination);
        for (Event event : events) {
            event.setConfirmedRequests(getConfirmedRequestsByEvent(event));
        }
        return events.stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event event = getEventIfExistOrThrow(eventId);
        if (updateEventDto.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            validateEventDate(now, updateEventDto.getEventDate());
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getStateAction() != null) {
            if (!event.getState().equals(State.PENDING)) {
                throw new BadStateException("Cannot publish the event because it's not in the right state: PUBLISHED");
            }
            switch (updateEventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(State.REJECTED);
                    break;
                default:
                    throw new BadStateException("Invalid StateAction value");
            }
        }
        return EventMapper.toFullDto(eventRepository.save(updateEvent(event, updateEventDto)));
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                                  HttpServletRequest request) {
        validateRangeFromFilter(rangeStart, rangeEnd);
        recordHit(request);
        Pagination pagination = createPaginationWithFilterSort(from, size, sort);

        List<Event> events = eventRepository.findPublicEvents(text, categories, paid, rangeStart,
                rangeEnd, State.PUBLISHED, pagination);

        Map<Long, Long> hits = getStats(events);
        events.forEach(event -> event.setViews(hits.get(event.getId())));

        for (Event event : events) {
            event.setConfirmedRequests(getConfirmedRequestsByEvent(event));
        }

        List<EventShortDto> shortDtoList;
        if (Boolean.TRUE.equals(onlyAvailable)) {
            shortDtoList = events.stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit())
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toList());
        } else {
            shortDtoList = events.stream()
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toList());
        }
        return shortDtoList;
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = getEventIfExistOrThrow(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " не опубликовано");
        }
        event.setConfirmedRequests(getConfirmedRequestsByEvent(event));
        recordHit(request);
        Map<Long, Long> hits = getStats(List.of(event));
        event.setViews(hits.get(event.getId()));
        return EventMapper.toFullDto(event);
    }

    @Override
    public List<RequestDto> getRequestsByEvent(Long userId, Long eventId) {
        getUserIfExistOrThrow(userId);
        getEventByIdAndInitiatorId(userId, eventId);

        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestUpdateResponseDto changeRequestsState(Long userId, Long eventId,
                                                        RequestUpdateRequestDto requestUpdateRequestDto) {
        Event event = getEventIfExistAndUserIdIsOwnerOrThrow(eventId, userId);
        if (Boolean.FALSE.equals(event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            throw new ConstraintViolationException("Для события не требуется подтверждения заявок на участик");
        }

        List<Request> requests = new ArrayList<>();
        for (Long requestId : requestUpdateRequestDto.getRequestIds()) {
            requests.add(getRequestIfExistOrThrow(requestId));
        }

        for (Request request : requests) {
            if (!request.getStatus().equals(State.PENDING)) {
                throw new ConstraintViolationException("Request must have status PENDING");
            }
        }

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        long confirmedRequestsCount = requestRepository.findByEventAndStatus(event, State.CONFIRMED).size();
        Long participantLimit = event.getParticipantLimit();

        if (requestUpdateRequestDto.getStatus().equals(State.REJECTED.toString())) {
            for (Request request : requests) {
                request.setStatus(State.REJECTED);
                rejectedRequests.add(RequestMapper.toDto(requestRepository.save(request)));
            }
        } else if (requestUpdateRequestDto.getStatus().equals(State.CONFIRMED.toString())) {
            if (participantLimit != 0 && participantLimit <= confirmedRequestsCount) {
                throw new ConstraintViolationException("The participant limit has been reached");
            }
            for (Request request : requests) {
                if (participantLimit == 0 || participantLimit > confirmedRequestsCount) {
                    request.setStatus(State.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toDto(requestRepository.save(request)));
                    confirmedRequestsCount++;
                } else {
                    request.setStatus(State.REJECTED);
                    rejectedRequests.add(RequestMapper.toDto(requestRepository.save(request)));
                }
            }
        } else {
            throw new BadStateException("Неожидаемое значение в поле status");
        }

        return RequestUpdateResponseDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private void validateEventDate(LocalDateTime now, LocalDateTime eventDate) {
        if (now.plusHours(2).isAfter(eventDate)) {
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не " +
                    "наступила. Value: " + eventDate);
        }
    }

    private void validateRangeFromFilter(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && (rangeStart.isAfter(rangeEnd))) {
                throw new BadRequestException("Дата и время начала поиска событий не может быть меньше Даты и времени "
                        + "окончания событий");

        }
    }

    private Category getCategoryIfExistOrThrow(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Категория с id " +
                categoryId + " не найдена в системе"));
    }

    private User getUserIfExistOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден в системе"));
    }

    private Event getEventIfExistOrThrow(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId +
                " не найдено в системе"));
    }

    private Event getEventIfExistAndUserIdIsOwnerOrThrow(long eventId, long userId) {
        return eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено в системе"));
    }

    private Request getRequestIfExistOrThrow(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос на участие с id " +
                requestId + " не найден в системе"));
    }

    private Event updateEvent(Event eventToUpdate, UpdateEventDto updateEventDto) {
        if (updateEventDto.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            eventToUpdate.setCategory(getCategoryIfExistOrThrow(updateEventDto.getCategory()));
        }
        if (updateEventDto.getDescription() != null) {
            eventToUpdate.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getLocation() != null) {
            eventToUpdate.setLocation(LocationMapper.toModel(updateEventDto.getLocation()));
        }
        if (updateEventDto.getPaid() != null) {
            eventToUpdate.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null) {
            eventToUpdate.setTitle(updateEventDto.getTitle());
        }
        return eventToUpdate;
    }

    private Pagination createPaginationWithFilterSort(int from, int size, EventSort sort) {
        if (sort == EventSort.VIEWS) {
            return new Pagination(from, size, Sort.by("views"));
        } else if (sort == EventSort.EVENT_DATE) {
            return new Pagination(from, size, Sort.by("eventDate"));
        } else {
            return new Pagination(from, size, Sort.unsorted());
        }
    }

    private void recordHit(HttpServletRequest request) {
        statClient.createHit(EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private Map<Long, Long> getStats(List<Event> events) {
        final String min = "2023-07-01 00:00:00";
        final String max = "2030-07-01 00:00:00";
        final String eventsUri = "/events/";

        List<String> uris = events.stream()
                .map(event -> eventsUri + event.getId())
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statClient.getStats(min, max, String.join(",", uris), true);
        List<ViewStatsDto> viewStatsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});
        Map<Long, Long> hits = new HashMap<>();

        for (ViewStatsDto statsDto : viewStatsDto) {
            String uri = statsDto.getUri();
            hits.put(Long.parseLong(uri.substring(eventsUri.length())), statsDto.getHits());
        }
        return hits;
    }

    private Long getConfirmedRequestsByEvent(Event event) {
        return (long) requestRepository.findByEventAndStatus(event, State.CONFIRMED).size();
    }
}
