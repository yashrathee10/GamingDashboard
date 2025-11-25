package com.example.demo.service;

import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.UserModel;

import java.util.Map;
import java.util.Set;

public interface UserService {
  ApiResponseObject<UserModel> createNewUser(UserModel userModel);

  ApiResponseObject<UserModel> getUserByUserId(Long userId);

  Map<Long, UserModel> getUserModelMapForUserIds(Set<String> userIds);

  LoginResponse login(LoginRequest request);
}
