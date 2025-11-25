package com.example.demo.service.impl;

import com.example.demo.entity.LeaderBoard;
import com.example.demo.enums.ApiStatusCode;
import com.example.demo.exception.ApiException;
import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.LeaderBoardModel;
import com.example.demo.model.LeaderBoardPageModel;
import com.example.demo.model.UserModel;
import com.example.demo.model.UserRefreshModel;
import com.example.demo.repository.LeaderBoardRepository;
import com.example.demo.service.LeaderBoardService;
import com.example.demo.service.UserService;
import com.example.demo.util.LeaderBoardRedisUtility;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service("com.example.demo.service.impl.LeaderBoardServiceImpl")
public class LeaderBoardServiceImpl implements LeaderBoardService {

  private final LeaderBoardRepository leaderBoardRepository;

  private final UserService userService;

  private final LeaderBoardRedisUtility leaderBoardRedisUtility;


  public LeaderBoardServiceImpl(LeaderBoardRepository leaderBoardRepository,
      UserService userService, LeaderBoardRedisUtility leaderBoardRedisUtility) {
    this.leaderBoardRepository = leaderBoardRepository;
    this.userService = userService;
    this.leaderBoardRedisUtility = leaderBoardRedisUtility;
  }

  @Override
  public LeaderBoardModel incrementScoreInLeaderBoardForUser(Long userId, Long score) {
    leaderBoardRedisUtility.incrementScoreForUser(String.valueOf(userId), score);
    LeaderBoard leaderBoard = updateLeaderBoardScoreAndRank(userId, score, Boolean.TRUE);
    return leaderBoard.toModel();
  }

  private LeaderBoard updateLeaderBoardScoreAndRank(Long userId, Long score, boolean isIncrement) {
    LeaderBoard leaderBoard = leaderBoardRepository.findByUserId(userId);
    Long rank = leaderBoardRedisUtility.getRankForUser(String.valueOf(userId));
    if (Objects.nonNull(leaderBoard)) {
      leaderBoard = leaderBoard.toBuilder()
          .totalScore(isIncrement ? leaderBoard.getTotalScore() + score : score).rank(rank).build();
    } else {
      leaderBoard = LeaderBoard.builder().userId(userId).totalScore(score).rank(rank).build();
    }
    leaderBoardRepository.save(leaderBoard);
    return leaderBoard;
  }

  @Override
  @Cacheable(value = "topUsersLeaderboard",
      key = "'top' + #pageable.pageNumber + '_' + #pageable.pageSize")
  public ApiResponseObject<LeaderBoardPageModel> getTopUsersFromLeaderboard(Pageable pageable) {
    ApiResponseObject<LeaderBoardPageModel> response =
        new ApiResponseObject<>(ApiStatusCode.SUCCESS);
    try {
      response.setResponseObject(new LeaderBoardPageModel(fetchTopUsersFromRedis(pageable)));
    } catch (RedisConnectionFailureException ex) {
      response.setResponseObject(new LeaderBoardPageModel(fetchTopUsersFromDatabase(pageable)));
    }
    return response;
  }

  private Page<UserModel> fetchTopUsersFromDatabase(Pageable pageable) {
    Page<UserModel> userModels;
    Page<LeaderBoard> leaderBoards = leaderBoardRepository.findAllByOrderByRankDesc(pageable);
    if (leaderBoards.hasContent()) {
      Map<Long, UserModel> userIdToUserModelMap = userService.getUserModelMapForUserIds(
          leaderBoards.stream().map(leaderBoard -> leaderBoard.getUserId().toString())
              .collect(Collectors.toSet()));
      userModels = new PageImpl<>(leaderBoards.stream().map(
          leaderBoard -> new UserModel(leaderBoard,
              userIdToUserModelMap.get(leaderBoard.getUserId()))).toList(), pageable,
          leaderBoards.getTotalElements());
    } else {
      userModels = new PageImpl<>(new ArrayList<>());
    }
    return userModels;
  }

  private Page<UserModel> fetchTopUsersFromRedis(Pageable pageable) {
    Long start = pageable.getOffset();
    Long end = pageable.getOffset() + pageable.getPageSize() - 1;
    Set<String> userIds = leaderBoardRedisUtility.getTopUsersInRange(start, end);

    List<UserModel> result = new ArrayList<>();

    Map<Long, UserModel> userIdToUserModelMap = userService.getUserModelMapForUserIds(userIds);
    if (userIds == null || userIds.isEmpty()) {
      return new PageImpl<>(result);
    } else {
      for (String userIdStr : userIds) {
        UserModel userModel = updateUserModelWithRankAndScoreFromRedis(
            userIdToUserModelMap.get(Long.parseLong(userIdStr)));
        result.add(userModel);
      }
      return new PageImpl<>(result, pageable, leaderBoardRedisUtility.getTotalUsers());
    }
  }

  @Override
  public ApiResponseObject<UserModel> getRankByUserId(Long userId) {
    ApiResponseObject<UserModel> responseObject = new ApiResponseObject<>(ApiStatusCode.SUCCESS);
    ApiResponseObject<UserModel> userModelResponseObject = userService.getUserByUserId(userId);
    if (userModelResponseObject.isValid()) {
      UserModel userModel = userModelResponseObject.getResponseObject();
      try {
        UserModel updatedUserModel = updateUserModelWithRankAndScoreFromRedis(userModel);
        responseObject.setResponseObject(updatedUserModel);
      } catch (Exception ex) {
        LeaderBoard leaderBoard = leaderBoardRepository.findByUserId(userId);
        responseObject.setResponseObject(new UserModel(leaderBoard, userModel));
      }
      return responseObject;
    } else {
      throw new ApiException("User Not Found", ApiStatusCode.NOT_FOUND);
    }
  }

  private UserModel updateUserModelWithRankAndScoreFromRedis(UserModel userModel) {
    Long rank = leaderBoardRedisUtility.getRankForUser(String.valueOf(userModel.getId()));
    Long score = leaderBoardRedisUtility.getScoreForUser(String.valueOf(userModel.getId()));
    if (Objects.nonNull(rank) && Objects.nonNull(score)) {
      return userModel.toBuilder().totalScore(score).rank(rank).build();
    } else {
      throw new ApiException("User Not Found In Redis", ApiStatusCode.NOT_FOUND);
    }
  }

  @Override
  public ApiResponseObject<Boolean> refreshRanks() {
    ApiResponseObject<Boolean> responseObject = new ApiResponseObject<>(ApiStatusCode.SUCCESS);
    try {
      Pageable pageable = PageRequest.of(0, 10000);
      Page<UserRefreshModel> userRefreshModels;
      do {
        userRefreshModels = leaderBoardRepository.getUserRefreshModelInPage(pageable);
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();

        for (UserRefreshModel user : userRefreshModels) {
          ZSetOperations.TypedTuple<String> tuple =
              new DefaultTypedTuple<>(user.getUserId().toString(),
                  user.getTotalScore().doubleValue());
          tuples.add(tuple);
        }

        leaderBoardRedisUtility.addMultipleUsers(tuples);
        pageable = pageable.next();
      } while (userRefreshModels.hasNext());
      responseObject.setResponseObject(Boolean.TRUE);
    } catch (Exception e) {
      responseObject.setResponseObject(Boolean.FALSE);
      responseObject.setMessage(e.getMessage());
    }
    return responseObject;
  }

  @Override
  public void refreshScoreAndRankInLeaderBoard(Long userId, Long updatedTotalScore) {
    leaderBoardRedisUtility.addOrOverwriteUser(String.valueOf(userId), updatedTotalScore);
    updateLeaderBoardScoreAndRank(userId, updatedTotalScore, Boolean.FALSE);
  }
}
