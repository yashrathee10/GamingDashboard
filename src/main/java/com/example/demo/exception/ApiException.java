package com.example.demo.exception;

import com.example.demo.enums.ApiStatusCode;

public class ApiException extends RuntimeException {
  private final ApiStatusCode status;

  public ApiException(String message, ApiStatusCode status) {
    super(message);
    this.status = status;
  }

  public ApiStatusCode getStatus() {
    return status;
  }
}
