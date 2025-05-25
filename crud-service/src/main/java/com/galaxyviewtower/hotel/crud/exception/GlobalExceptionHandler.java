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

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    Map<String, String> errors = new HashMap<>();
    ex.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            exchange.getRequest().getPath().value(),
            errors);

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
      IllegalArgumentException ex, ServerWebExchange exchange) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST, ex.getMessage(), exchange.getRequest().getPath().value(), null);
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
      Exception ex, ServerWebExchange exchange) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            exchange.getRequest().getPath().value(),
            Map.of("error", ex.getMessage()));
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
  }
}
