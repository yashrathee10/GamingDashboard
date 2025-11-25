package com.example.demo.service;

import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.GameSessionsModel;
import com.example.demo.model.UserModel;

public interface GameSessionsService {
  ApiResponseObject<UserModel> submitScores(GameSessionsModel gameSessionsModel);

  ApiResponseObject<Boolean> refreshScoreForAUser(Long userId);
}
