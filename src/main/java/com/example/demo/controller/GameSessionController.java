package com.example.demo.controller;

import com.example.demo.enums.ApiStatusCode;
import com.example.demo.model.ApiResponseObject;
import com.example.demo.model.GameSessionsModel;
import com.example.demo.model.UserModel;
import com.example.demo.service.GameSessionsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class GameSessionController {

  private final GameSessionsService gameSessionsService;

  public GameSessionController(GameSessionsService gameSessionsService) {
    this.gameSessionsService = gameSessionsService;
  }

  @PostMapping("/submit")
  public ResponseEntity<ApiResponseObject<UserModel>> submitScores(HttpServletRequest request,
      @RequestBody @Valid GameSessionsModel gameSessionsModel) {
    Integer authUserId = (Integer) request.getAttribute("authUserId");
    if (authUserId == null || !authUserId.equals(gameSessionsModel.getUserId().intValue())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponseObject<>("Unauthorized", ApiStatusCode.UNAUTHORIZED, null));
    }
    ApiResponseObject<UserModel> responseObject =
        gameSessionsService.submitScores(gameSessionsModel);
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }


  @PostMapping("/refresh/user/{user_id}")
  public ResponseEntity<ApiResponseObject<Boolean>> refreshScoreForAUser(
      @PathVariable("user_id") Long userId) {
    ApiResponseObject<Boolean> responseObject = gameSessionsService.refreshScoreForAUser(userId);
    return new ResponseEntity<>(responseObject, HttpStatus.OK);
  }
}
