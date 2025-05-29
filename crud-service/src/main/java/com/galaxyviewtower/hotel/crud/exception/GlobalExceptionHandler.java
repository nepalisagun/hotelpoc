package com.galaxyviewtower.hotel.crud.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  @Data
  public static class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, String> details;

    public ErrorResponse(
        HttpStatus status, String message, String path, Map<String, String> details) {
      this.timestamp = LocalDateTime.now();
      this.status = status.value();
      this.error = status.getReasonPhrase();
      this.message = message;
      this.path = path;
      this.details = details;
    }
  }

  @ExceptionHandler(ValidationException.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(ValidationException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Error");
    response.put("message", ex.getErrors());
    return Mono.just(ResponseEntity.badRequest().body(response));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleWebExchangeBindException(WebExchangeBindException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Error");
    response.put("message", ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList()));
    return Mono.just(ResponseEntity.badRequest().body(response));
  }

  @ExceptionHandler(JwtAuthenticationException.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.UNAUTHORIZED.value());
    response.put("error", "Authentication Error");
    response.put("message", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put("error", "Internal Server Error");
    response.put("message", "An unexpected error occurred");
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
  }
}
