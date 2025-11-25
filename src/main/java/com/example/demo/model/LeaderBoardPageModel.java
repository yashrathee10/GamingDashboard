package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LeaderBoardPageModel {
  private List<UserModel> content;
  private int page;
  private int size;
  private long totalElements;

  public LeaderBoardPageModel(Page<UserModel> page) {
    this.content = page.getContent();
    this.page = page.getNumber();
    this.size = page.getSize();
    this.totalElements = page.getTotalElements();
  }
}
