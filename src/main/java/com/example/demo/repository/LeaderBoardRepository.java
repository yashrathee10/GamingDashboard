package com.example.demo.repository;

import com.example.demo.entity.LeaderBoard;
import com.example.demo.model.UserRefreshModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LeaderBoardRepository extends JpaRepository<LeaderBoard, Long> {
  LeaderBoard findByUserId(Long userId);

  @Query("select new com.example.demo.model.UserRefreshModel(l.userId, l.totalScore) from "
      + "LeaderBoard l order by l.userId DESC")
  Page<UserRefreshModel> getUserRefreshModelInPage(Pageable pageable);

  Page<LeaderBoard> findAllByOrderByRankDesc(Pageable pageable);
}
