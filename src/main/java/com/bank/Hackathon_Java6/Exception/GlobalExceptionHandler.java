package com.bank.Hackathon_Java6.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bank.Hackathon_Java6.Dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(LoginRateLimitExceededException.class)
    public ResponseEntity<?> handleLoginRateLimit(LoginRateLimitExceededException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 429,
                        "error", "Too Many Requests",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomerNotFound(
            CustomerNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BankNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBankNotFound(
            BankNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(FavoriteAccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleFavoriteAccountNotFound(
            FavoriteAccountNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MaxFavoriteAccountsExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxAccountsExceeded(
            MaxFavoriteAccountsExceededException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI(), null);
    }

    private ResponseEntity<ErrorResponseDTO> buildError(
            HttpStatus status, String message, String path, Map<String, String> fieldErrors) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
