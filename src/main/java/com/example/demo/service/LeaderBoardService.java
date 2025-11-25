package com.example.demo.service;

import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.LeaderBoardModel;
import com.example.demo.model.LeaderBoardPageModel;
import com.example.demo.model.UserModel;
import org.springframework.data.domain.Pageable;

public interface LeaderBoardService {
  LeaderBoardModel incrementScoreInLeaderBoardForUser(Long userId, Long score);

  ApiResponseObject<LeaderBoardPageModel> getTopUsersFromLeaderboard(Pageable pageable);

  ApiResponseObject<UserModel> getRankByUserId(Long userId);

  ApiResponseObject<Boolean> refreshRanks();

  void refreshScoreAndRankInLeaderBoard(Long userId, Long updatedTotalScore);
}
