package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    @Email
    @NotBlank
    @Length(min = 6, max = 254, message = "Значение в поле email должно быть от 6 до 254 символов")
    private String email;

    @NotBlank
    @Length(min = 2, max = 250, message = "Значение в поле name должно быть от 2 до 250 символов")
    private String name;
}
