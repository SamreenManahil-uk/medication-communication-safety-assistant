package com.samreen.medicationsafety.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                    fieldErrors.put(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                );

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException exception) {

        return buildError(
                HttpStatus.NOT_FOUND,
                "Not Found",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(
            DuplicateResourceException exception) {

        return buildError(
                HttpStatus.CONFLICT,
                "Conflict",
                exception.getMessage()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException exception) {

        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid email or password"
        );
    }

    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status,
            String error,
            String message) {

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
