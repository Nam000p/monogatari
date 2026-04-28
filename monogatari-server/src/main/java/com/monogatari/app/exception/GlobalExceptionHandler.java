package com.monogatari.app.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequests(RuntimeException exception) {
        log.warn("Bad Request: {}", exception.getMessage());
        return buildErrorResponse("Bad Request", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException exception) {
        return buildErrorResponse("Not Found", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiRateLimitException.class)
    public ResponseEntity<Map<String, String>> handleApiRateLimitException(ApiRateLimitException exception) {
        return buildErrorResponse("Too Many Requests", exception.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception exception, HttpServletResponse response) {
       if (response.isCommitted()) {
          return null;
       }
       log.error("Internal Server Error: ", exception);
       return buildErrorResponse("Internal Error", "An unexpected error occurred: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(String error, String message, HttpStatus status) {
       Map<String, String> response = new HashMap<>();
       response.put("error", error);
       response.put("message", message);
       return new ResponseEntity<>(response, status);
    }
}