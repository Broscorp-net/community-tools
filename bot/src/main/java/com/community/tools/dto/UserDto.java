package com.community.tools.dto;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class UserDto {

  private final Long userID;
  private final String gitName;
  private final Integer points;
  private final Integer karma;
  private final Integer tasksDone;
  private final Date lastCommit;

  public UserDto(Long userID, String gitName, Integer points, Integer karma, Integer tasksDone,
      Date lastCommit) {
    this.userID = userID;
    this.gitName = gitName;
    this.karma = karma;
    this.tasksDone = tasksDone;
    this.points = points;
    this.lastCommit = lastCommit;
  }

}
