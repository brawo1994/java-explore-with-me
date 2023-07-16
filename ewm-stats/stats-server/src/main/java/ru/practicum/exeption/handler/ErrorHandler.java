package ru.practicum.exeption.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exeption.BadRequestException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestException(final BadRequestException e) {
        return new ErrorResponseDto("BAD REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
    }
}
