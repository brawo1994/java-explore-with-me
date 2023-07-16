package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class CompilationDto {

    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    private String title;
}
