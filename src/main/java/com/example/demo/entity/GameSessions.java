package com.example.demo.entity;

import com.example.demo.enums.GameMode;
import com.example.demo.model.GameSessionsModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "game_sessions")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameSessions {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "score")
  private Long score;

  @Enumerated(EnumType.STRING)
  @Column(name = "game_mode")
  private GameMode gameMode;

  @Column(name = "timestamp")
  private Timestamp timestamp;

  public GameSessionsModel toModel() {
    return GameSessionsModel.builder().id(id).userId(userId).score(score).gameMode(gameMode)
        .timestamp(timestamp).build();
  }
}
