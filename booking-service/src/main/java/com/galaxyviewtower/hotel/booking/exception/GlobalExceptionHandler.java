package com.galaxyviewtower.hotel.booking.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(HotelServiceException.class)
  public ResponseEntity<Map<String, Object>> handleHotelServiceException(
      HotelServiceException ex, ServerWebExchange exchange) {
    log.error("Hotel service exception occurred: ", ex);
    
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Bad Request");
    errorResponse.put("message", ex.getMessage());
    errorResponse.put("hotelId", ex.getHotelId());
    errorResponse.put("errorType", ex.getErrorType());
    
    if (exchange != null) {
      errorResponse.put("path", exchange.getRequest().getPath().value());
    }
    
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(
      RuntimeException ex, ServerWebExchange exchange) {
    log.error("Runtime exception occurred: ", ex);
    
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Bad Request");
    errorResponse.put("message", "An unexpected error occurred. Please try again later.");
    errorResponse.put("details", ex.getMessage());
    
    if (exchange != null) {
      errorResponse.put("path", exchange.getRequest().getPath().value());
    }
    
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(ServerWebInputException.class)
  public ResponseEntity<Map<String, Object>> handleServerWebInputException(
      ServerWebInputException ex, ServerWebExchange exchange) {
    log.error("Invalid input exception: ", ex);
    
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Bad Request");
    errorResponse.put("message", "Invalid input data. Please check your request format.");
    errorResponse.put("details", ex.getReason());
    errorResponse.put("path", exchange.getRequest().getPath().value());
    
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, ServerWebExchange exchange) {
    log.error("Unexpected error occurred: ", ex);
    
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.put("error", "Internal Server Error");
    errorResponse.put("message", "An unexpected error occurred. Please try again later.");
    errorResponse.put("path", exchange.getRequest().getPath().value());
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
} 