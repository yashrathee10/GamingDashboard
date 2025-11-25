package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
  List<User> findByIdIn(Set<String> userIds);

  // For fetching all the users with mismatched totalScore
  @Query(value = """
      SELECT u.id
      FROM users u
      LEFT JOIN game_sessions gs ON gs.user_id = u.id
      LEFT JOIN leaderboard lb ON lb.user_id = u.id
      GROUP BY u.id, lb.total_score
      HAVING COALESCE(SUM(gs.score), 0) <> lb.total_score
      """, countQuery = """
      SELECT COUNT(*)
      FROM (
          SELECT u.id
          FROM users u
          LEFT JOIN game_sessions gs ON gs.user_id = u.id
          LEFT JOIN leaderboard lb ON lb.user_id = u.id
          GROUP BY u.id, lb.total_score
          HAVING COALESCE(SUM(gs.score), 0) <> lb.total_score
      ) AS count_table
      """, nativeQuery = true)
  Page<Long> findUsersWithMismatchedScores(Pageable pageable);

  Optional<User> findByUsername(String userName);
}
