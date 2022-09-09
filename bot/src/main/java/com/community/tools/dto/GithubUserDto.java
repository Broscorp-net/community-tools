package com.community.tools.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class GithubUserDto {

  private final String gitName;
  private final LocalDate lastCommit;
  private List<GithubRepositoryDto> repositories;
  private int totalPoints;
  private int completedTasks;
}
