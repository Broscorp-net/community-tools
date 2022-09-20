package com.community.tools.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserForLeaderboardDto {

  private final String gitName;
  private final LocalDate dateLastActivity;
  private final Integer completedTasks;
  private final Integer pointByTask;

  //TODO fill this field
  private Integer karma = 0;
  private final Integer totalPoints;

  /**
   * Constructor for DTO.
   * @param gitName - gitName
   * @param dateLastActivity - dateLastActivity
   * @param completedTasks - completedTasks
   * @param pointByTask - pointByTask
   */
  public UserForLeaderboardDto(String gitName, LocalDate dateLastActivity, Integer completedTasks,
      Integer pointByTask) {
    this.gitName = gitName;
    this.dateLastActivity = dateLastActivity;
    this.completedTasks = completedTasks;
    this.pointByTask = pointByTask;
    this.totalPoints = pointByTask + this.karma;
  }

}
