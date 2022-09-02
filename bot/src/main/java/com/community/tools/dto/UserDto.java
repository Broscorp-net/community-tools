package com.community.tools.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {

  private final Long userID;
  private final String gitName;
  private final Integer points;
  private final Integer karma;
  private final Integer tasksDone;
  private final Date lastCommit;

}
