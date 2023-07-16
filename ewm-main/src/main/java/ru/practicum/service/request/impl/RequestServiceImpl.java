package ru.practicum.service.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exeption.ConstraintViolationException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.mapper.request.RequestMapper;
import ru.practicum.model.enums.State;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.request.RequestRepository;
import ru.practicum.repository.user.UserRepository;
import ru.practicum.service.request.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<RequestDto> getRequests(Long userId) {
        User user = getUserIfExistOrThrow(userId);
        List<Request> requests = requestRepository.findByRequester(user);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = getUserIfExistOrThrow(userId);
        Event event = getEventIfExistOrThrow(eventId);
        event.setConfirmedRequests((long) requestRepository.findByEventAndStatus(event, State.CONFIRMED).size());

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConstraintViolationException("Нельзя создать запрос в свое событие");
        }

        if (event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED)) {
            throw new ConstraintViolationException("Нельзя создать запрос в не опубликованное событие");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConstraintViolationException("Количество запросов в событие достигло максимума");
        }

        requestRepository.findByEventAndRequester(event, user).ifPresent(request -> {
            throw new ConstraintViolationException("Запрос уже существует");
        });

        State status;
        if (Boolean.FALSE.equals(event.getRequestModeration()) || (event.getParticipantLimit() == 0)) {
            status = State.CONFIRMED;
        } else {
            status = State.PENDING;
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(status)
                .build();
        log.info("Request with id: {} added to DB", request.getId());
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        getUserIfExistOrThrow(userId);
        Request request = getRequestIfExistOrThrow(requestId);
        request.setStatus(State.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    private User getUserIfExistOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден в системе"));
    }

    private Event getEventIfExistOrThrow(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId +
                " не найдено в системе"));
    }

    private Request getRequestIfExistOrThrow(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос на участие с id " +
                requestId + " не найден в системе"));
    }
}
