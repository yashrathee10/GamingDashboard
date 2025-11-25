package com.example.demo.entity;

import com.example.demo.model.LeaderBoardModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaderboard")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LeaderBoard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "total_score")
  private Long totalScore;

  @Column(name = "\"rank\"")
  private Long rank;

  public LeaderBoardModel toModel() {
    return LeaderBoardModel.builder().id(id).userId(userId).totalScore(totalScore).rank(rank)
        .build();
  }
}
