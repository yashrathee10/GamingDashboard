package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LeaderBoardModel {
  private Long id;
  private Long userId;
  private Long totalScore;
  private Long rank;
}
