package kz.iitu.hello.exception;

import jakarta.servlet.http.HttpServletRequest;
import kz.iitu.hello.web.dto.PanMaratErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class PanMaratGlobalExceptionHandler {

    @ExceptionHandler(PanMaratEntityNotFoundException.class)
    public ResponseEntity<PanMaratErrorResponse> handleNotFound(PanMaratEntityNotFoundException ex, HttpServletRequest request) {
        log.error("Not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(buildErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(PanMaratBusinessException.class)
    public ResponseEntity<PanMaratErrorResponse> handleConflict(PanMaratBusinessException ex, HttpServletRequest request) {
        log.error("Business conflict: {}", ex.getMessage());
        return ResponseEntity.status(409).body(buildErrorResponse(409, "Conflict", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler({PanMaratCourseLimitExceededException.class, IllegalArgumentException.class})
    public ResponseEntity<PanMaratErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(400, "Bad Request", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PanMaratErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("Validation failed at {}: {}", request.getRequestURI(), validationErrors);
        return ResponseEntity.badRequest().body(buildErrorResponse(400, "Bad Request", "Validation failed", request.getRequestURI(), validationErrors));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<PanMaratErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(404).body(buildErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PanMaratErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(buildErrorResponse(500, "Internal Server Error", "An unexpected error occurred", request.getRequestURI(), null));
    }

    private PanMaratErrorResponse buildErrorResponse(int status,
                                                     String error,
                                                     String message,
                                                     String path,
                                                     Map<String, String> validationErrors) {
        return new PanMaratErrorResponse(LocalDateTime.now(), status, error, message, path, validationErrors);
    }
}
