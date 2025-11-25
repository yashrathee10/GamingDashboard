package com.example.demo.service.impl;

import com.example.demo.enums.ApiStatusCode;
import com.example.demo.exception.ApiException;
import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.GameSessionsModel;
import com.example.demo.model.LeaderBoardModel;
import com.example.demo.model.UserModel;
import com.example.demo.repository.GameSessionsRepository;
import com.example.demo.service.GameSessionsService;
import com.example.demo.service.LeaderBoardService;
import com.example.demo.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.example.demo.service.impl.GameSessionsImpl")
public class GameSessionsImpl implements GameSessionsService {

  private final GameSessionsRepository gameSessionsRepository;

  private final LeaderBoardService leaderBoardService;

  private final UserService userService;

  public GameSessionsImpl(GameSessionsRepository gameSessionsRepository,
      LeaderBoardService leaderBoardService, UserService userService) {
    this.gameSessionsRepository = gameSessionsRepository;
    this.leaderBoardService = leaderBoardService;
    this.userService = userService;
  }

  @Override
  @Transactional
  @CacheEvict(value = "topUsersLeaderboard", allEntries = true)
  public ApiResponseObject<UserModel> submitScores(GameSessionsModel gameSessionsModel) {
    ApiResponseObject<UserModel> userModelResponseObject =
        userService.getUserByUserId(gameSessionsModel.getUserId());
    if (userModelResponseObject.isValid()) {
      UserModel userModel = userModelResponseObject.getResponseObject();
      gameSessionsRepository.save(gameSessionsModel.toEntity());
      LeaderBoardModel leaderBoardModel =
          leaderBoardService.incrementScoreInLeaderBoardForUser(gameSessionsModel.getUserId(),
              gameSessionsModel.getScore());
      return new ApiResponseObject<>(userModel.toBuilder().rank(leaderBoardModel.getRank())
          .totalScore(leaderBoardModel.getTotalScore()).build(), ApiStatusCode.SUCCESS);
    } else {
      throw new ApiException("User Not Found", ApiStatusCode.NOT_FOUND);
    }
  }

  @Override
  @Transactional
  public ApiResponseObject<Boolean> refreshScoreForAUser(Long userId) {
    ApiResponseObject<Boolean> responseObject = new ApiResponseObject<>(ApiStatusCode.SUCCESS);
    try {
      Long updatedTotalScore = gameSessionsRepository.getSumOfScoresByUserId(userId);
      leaderBoardService.refreshScoreAndRankInLeaderBoard(userId, updatedTotalScore);
      responseObject.setResponseObject(Boolean.TRUE);
    } catch (Exception e) {
      responseObject.setResponseObject(Boolean.FALSE);
      responseObject.setMessage(e.getMessage());
    }
    return responseObject;
  }
}
