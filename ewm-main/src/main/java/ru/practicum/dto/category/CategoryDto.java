package ru.practicum.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank
    @Length(min = 1, max = 50, message = "Значение в поле name должно быть от 1 до 50 символов")
    private String name;
}
