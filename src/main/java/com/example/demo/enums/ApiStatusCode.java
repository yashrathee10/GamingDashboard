package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ApiStatusCode {
  SUCCESS(1000),
  FAILED(1001),
  VALIDATION_ERROR(1002),
  NOT_FOUND(1003),
  UNAUTHORIZED(1004),
  PROCESSING_ERROR(9999);

  private final int code;

  ApiStatusCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public String getName() {
    return this.name(); // required for JSON enum serialization
  }

}
