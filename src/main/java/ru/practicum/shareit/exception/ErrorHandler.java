package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final ValidationException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(final ConflictException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAccessDenied(final AccessDeniedException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
