package com.community.tools.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserForLeaderboardDto {

  private final String gitName;
  private final LocalDate lastCommit;
  private final Integer tasksDone;
  private final Integer pointsForTasks;

}
