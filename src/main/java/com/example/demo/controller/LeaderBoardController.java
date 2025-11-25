package com.example.demo.controller;

import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.LeaderBoardPageModel;
import com.example.demo.model.UserModel;
import com.example.demo.service.GameSessionsService;
import com.example.demo.service.LeaderBoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderBoardController {

  private final LeaderBoardService leaderBoardService;

  public LeaderBoardController(GameSessionsService gameSessionsService,
      LeaderBoardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
  }

  @GetMapping("/top")
  public ResponseEntity<ApiResponseObject<LeaderBoardPageModel>> getTopUsers(
      @PageableDefault(size = 10) Pageable pageable) {
    ApiResponseObject<LeaderBoardPageModel> responseObject =
        leaderBoardService.getTopUsersFromLeaderboard(pageable);
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }

  @GetMapping("/rank/{user_id}")
  public ResponseEntity<ApiResponseObject<UserModel>> getUserRankById(
      @PathVariable("user_id") Long userId) {
    ApiResponseObject<UserModel> responseObject = leaderBoardService.getRankByUserId(userId);
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }

  @PostMapping("/refresh-ranks")
  public ResponseEntity<ApiResponseObject<Boolean>> refreshRanks() {
    ApiResponseObject<Boolean> responseObject = leaderBoardService.refreshRanks();
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }
}
