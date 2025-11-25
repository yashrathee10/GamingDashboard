package com.example.demo.model;

import com.example.demo.entity.LeaderBoard;
import com.example.demo.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.sql.Timestamp;
import java.util.Objects;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {

  private Long id;

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @JsonProperty("total_score")
  private Long totalScore;

  private Long rank;

  @JsonProperty("join_date")
  private Timestamp joinDate;

  public UserModel(LeaderBoard leaderBoard, UserModel userModel) {
    this.id = userModel.getId();
    this.username = userModel.getUsername();
    this.totalScore = leaderBoard.getTotalScore();
    this.rank = leaderBoard.getRank();
    this.joinDate = userModel.getJoinDate();
  }

  public User toEntity() {
    return User.builder().username(username).password(password)
        .joinDate(Objects.nonNull(joinDate) ? joinDate : new Timestamp(System.currentTimeMillis()))
        .build();
  }

  private static final PolicyFactory SANITIZER =
      Sanitizers.FORMATTING.and(Sanitizers.LINKS);

  public void setUsername(String username) {
    this.username = SANITIZER.sanitize(username);
  }

  public void setPassword(String password) {
    this.password = SANITIZER.sanitize(password);
  }
}
