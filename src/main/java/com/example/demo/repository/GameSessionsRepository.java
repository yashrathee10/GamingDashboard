package com.example.demo.repository;

import com.example.demo.entity.GameSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameSessionsRepository extends JpaRepository<GameSessions, Long> {

  @Query("select sum(g.score) from GameSessions g where g.userId=:userId")
  Long getSumOfScoresByUserId(Long userId);
}
