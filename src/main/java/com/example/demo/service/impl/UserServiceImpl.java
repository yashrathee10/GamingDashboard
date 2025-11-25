package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.enums.ApiStatusCode;
import com.example.demo.exception.ApiException;
import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("com.example.demo.service.impl.UserServiceImpl")
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public ApiResponseObject<UserModel> createNewUser(UserModel userModel) {
    User user = userRepository.save(userModel.toEntity());
    return new ApiResponseObject<>(user.toModel(), ApiStatusCode.SUCCESS);
  }

  @Override
  public ApiResponseObject<UserModel> getUserByUserId(Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isPresent()) {
      return new ApiResponseObject<>(userOptional.get().toModel(), ApiStatusCode.SUCCESS);
    } else {
      throw new ApiException("User Not Found", ApiStatusCode.NOT_FOUND);
    }
  }

  @Override
  public Map<Long, UserModel> getUserModelMapForUserIds(Set<String> userIds) {
    List<User> userList = userRepository.findByIdIn(userIds);
    return userList.stream().collect(Collectors.toMap(User::getId, User::toModel));
  }

  @Override
  public LoginResponse login(LoginRequest request) {

    User user = userRepository.findByUsername(request.getUserName())
        .orElseThrow(() -> new ApiException("User Not Found", ApiStatusCode.NOT_FOUND));

    if (!user.getPassword().equals(request.getPassword())) {
      throw new ApiException("User Not Found", ApiStatusCode.VALIDATION_ERROR);
    }
    String token = JwtUtility.generateToken(user.getId(), user.getUsername());

    return new LoginResponse(token, user.getId(), user.getUsername());
  }
}
