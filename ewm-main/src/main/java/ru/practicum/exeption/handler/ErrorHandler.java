package ru.practicum.exeption.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exeption.BadRequestException;
import ru.practicum.exeption.BadStateException;
import ru.practicum.exeption.ConstraintViolationException;
import ru.practicum.exeption.NotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        return new ErrorResponseDto("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestException(final BadRequestException e) {
        return new ErrorResponseDto("BAD REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFoundException(final NotFoundException e) {
        return new ErrorResponseDto("NOT_FOUND", "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponseDto("CONFLICT", "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ErrorResponseDto("CONFLICT", "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleBadStateException(final BadStateException e) {
        return new ErrorResponseDto("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now());
    }
}
