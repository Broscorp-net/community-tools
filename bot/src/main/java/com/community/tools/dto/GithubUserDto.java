package com.community.tools.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
  private Optional<Integer> totalPoints;
  private Optional<Integer> completedTasks;

}
