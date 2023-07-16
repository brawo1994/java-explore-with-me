package ru.practicum.mapper.request;

import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.request.Request;

public final class RequestMapper {

    private RequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
