package com.example.demo.model;

import com.example.demo.entity.GameSessions;
import com.example.demo.enums.GameMode;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameSessionsModel {

  private Long id;

  @JsonProperty("user_id")
  @NotNull
  private Long userId;

  @NotNull
//  @Min(0)
//  @Max(1000)
  private Long score;

  @JsonProperty("game_mode")
  private GameMode gameMode;

  private Timestamp timestamp;

  public GameSessions toEntity() {
    return GameSessions.builder().id(id).userId(userId).score(score)
        .gameMode(Objects.nonNull(gameMode) ? gameMode : GameMode.solo).build();
  }
}
