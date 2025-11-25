package com.example.demo.model;

import com.example.demo.enums.ApiStatusCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiStatusCodeModel {

  private String name;
  private int code;

  public ApiStatusCodeModel(ApiStatusCode status) {
    this.name = status.name();
    this.code = status.getCode();
  }
}
