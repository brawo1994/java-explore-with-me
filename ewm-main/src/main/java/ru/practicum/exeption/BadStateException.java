package ru.practicum.exeption;

public class BadStateException extends RuntimeException {
    public BadStateException(String message) {
        super(message);
    }
}
