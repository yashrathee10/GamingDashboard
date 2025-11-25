package com.example.demo.controller;

import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.UserModel;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<ApiResponseObject<UserModel>> createNewUser(
      @RequestBody @Valid UserModel userModel) {
    ApiResponseObject<UserModel> responseObject = userService.createNewUser(userModel);
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponseObject<UserModel>> getUserByUserId(
      @PathVariable("userId") Long userId) {
    ApiResponseObject<UserModel> responseObject = userService.getUserByUserId(userId);
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    LoginResponse response = userService.login(request);

    return ResponseEntity.ok(response);
  }
}
