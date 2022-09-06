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

  public Integer getTotalPoints() {
    return repositories
        .stream()
        .mapToInt(GithubRepositoryDto::getPoints)
        .sum();
  }

  public Optional<GithubRepositoryDto> getRepositoryWithName(String name) {
    return repositories
        .stream()
        .filter(repositoryDto -> repositoryDto.getTaskName().equals(name))
        .findFirst();
  }

  public Integer getCompletedTasks() {
    return repositories
        .stream()
        .mapToInt(value -> {
          if (value.getLastBuildStatus().equals("success")) {
            return 1;
          } else {
            return 0;
          }
        })
        .sum();
  }

}
