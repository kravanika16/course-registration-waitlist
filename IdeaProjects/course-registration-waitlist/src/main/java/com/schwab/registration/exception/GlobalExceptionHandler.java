package com.schwab.registration.exception;

import com.schwab.registration.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLearnerException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidLearner(InvalidLearnerException exception, HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidCourseCapacityException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCapacity(InvalidCourseCapacityException exception, HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(LearnerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleLearnerNotFound(LearnerNotFoundException exception, HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path) {

        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(response);
    }
}
