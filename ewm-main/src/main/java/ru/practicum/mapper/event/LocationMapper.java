package ru.practicum.mapper.event;

import ru.practicum.dto.event.LocationDto;
import ru.practicum.model.event.Location;

public final class LocationMapper {

    private LocationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Location toModel(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
