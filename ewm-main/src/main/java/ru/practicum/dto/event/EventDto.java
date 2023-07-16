package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.model.enums.StateAction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class EventDto {

    @NotBlank
    @Length(min = 20, max = 2000, message = "Значение в поле annotation должно быть от 20 до 2000 символов")
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Length(min = 20, max = 7000, message = "Значение в поле description должно быть от 20 до 7000 символов")
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Length(min = 3, max = 120, message = "Значение в поле title должно быть от 3 до 120 символов")
    private String title;

    private StateAction stateAction;
}
