package com.galaxyviewtower.hotel.crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<String>> handleValidationException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    StringBuilder errors = new StringBuilder("Validation failed: ");
    ex.getFieldErrors()
        .forEach(
            error ->
                errors
                    .append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; "));
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString()));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<String>> handleGenericException(
      Exception ex, ServerWebExchange exchange) {
    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Internal server error: " + ex.getMessage()));
  }
}
