package com.community.tools.dto;

import lombok.Getter;

@Getter
public class UserDto {

  private final Long userID;
  private final String gitName;
  private final Integer points;
  private final Integer karma;
  private final Integer tasksDone;

  public UserDto(Long userID, String gitName, Integer points, Integer karma, Integer tasksDone) {
    this.userID = userID;
    this.gitName = gitName;
    this.karma = karma;
    this.tasksDone = tasksDone;
    this.points = points;
  }

}
