package com.community.tools.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserForLeaderboardDto {

  private final String gitName;
  //TODO add this field
  private LocalDate dateRegistration = null;
  private final LocalDate dateLastActivity;
  private final Integer completedTasks;
  private final Integer pointByTask;
  //TODO add this field
  private Integer karma = 0;
  private final Integer totalPoints;

  public UserForLeaderboardDto(String gitName, LocalDate dateLastActivity, Integer completedTasks,
      Integer pointByTask) {
    this.gitName = gitName;
    this.dateLastActivity = dateLastActivity;
    this.completedTasks = completedTasks;
    this.pointByTask = pointByTask;
    this.totalPoints = pointByTask + this.karma;
  }

}
