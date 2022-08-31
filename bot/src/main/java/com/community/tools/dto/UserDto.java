package com.community.tools.dto;

import lombok.Getter;

@Getter
public class UserDto {

  private final String userID;
  private final String gitName;

  public UserDto(String userID, String gitName) {
    this.userID = userID;
    this.gitName = gitName;
  }

}
