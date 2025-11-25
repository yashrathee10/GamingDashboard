package com.example.demo.executor;

import com.example.demo.service.LeaderBoardService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@EnableScheduling
@Component("com.example.demo.executor.UserSyncExecutor")
public class RankSyncExecutor {

  private final LeaderBoardService leaderBoardService;

  public RankSyncExecutor(LeaderBoardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
  }

  @Scheduled(cron = "${gamingDashboard.sync.ranks.cron}", zone = "UTC")
  public void syncRanks() {
    leaderBoardService.refreshRanks();
  }
}
