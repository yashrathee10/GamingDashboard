package com.example.demo.exception;

import com.example.demo.enums.ApiStatusCode;
import com.example.demo.model.ApiResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponseObject<?>> handleCustomApiException(ApiException ex) {
    ApiResponseObject<?> response = new ApiResponseObject<>(ex.getMessage(), ex.getStatus(), null);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseObject<?>> handleAllExceptions(Exception ex) {
    ApiResponseObject<?> response =
        new ApiResponseObject<>(ex.getMessage(), ApiStatusCode.PROCESSING_ERROR, null);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
