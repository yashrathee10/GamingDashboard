package com.example.demo.model;

import com.example.demo.enums.ApiStatusCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseObject<T> {
  private String message;
  private ApiStatusCodeModel status;
  private T responseObject;

  public ApiResponseObject(ApiStatusCode status) {
    this.status = new ApiStatusCodeModel(status);
  }

  public ApiResponseObject(T responseObject, ApiStatusCode status) {
    this.status = new ApiStatusCodeModel(status);
    this.responseObject = responseObject;
  }

  public ApiResponseObject(String message, ApiStatusCode status, T responseObject) {
    this.status = new ApiStatusCodeModel(status);
    this.message = message;
    this.responseObject = responseObject;
  }

  @JsonIgnore
  public boolean isValid() {
    return ApiStatusCode.SUCCESS.getName().equals(this.getStatus().getName())
        && this.getResponseObject() != null;
  }
}
