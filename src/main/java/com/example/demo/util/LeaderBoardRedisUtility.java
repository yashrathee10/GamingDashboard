package com.example.demo.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LeaderBoardRedisUtility {

  private static final String LEADERBOARD_KEY = "leaderboard";

  private final StringRedisTemplate redisTemplate;

  public LeaderBoardRedisUtility(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public Set<String> getTopUsersInRange(Long start, Long end) {
    return redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, start, end);
  }

  public Long getScoreForUser(String userIdStr) {
    Double score = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, userIdStr);
    return score != null ? score.longValue() : null;
  }

  public Long getRankForUser(String userIdStr) {
    Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, userIdStr);
    return (rank != null) ? rank + 1 : null;
  }

  public Long getTotalUsers() {
    return redisTemplate.opsForZSet().size(LEADERBOARD_KEY);
  }

  public void addMultipleUsers(Set<ZSetOperations.TypedTuple<String>> tuples) {
    redisTemplate.opsForZSet().add(LEADERBOARD_KEY, tuples);
  }

  public void incrementScoreForUser(String userId, Long score) {
    redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, String.valueOf(userId), score);
  }

  public void addOrOverwriteUser(String userId, Long updatedTotalScore) {
    redisTemplate.opsForZSet().add(LEADERBOARD_KEY, String.valueOf(userId), updatedTotalScore);
  }
}
