package com.community.tools.dto;

import com.community.tools.model.TaskNameAndStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

  //TODO delete it
  public Integer getAmountOfSuccessfulBuilds() {
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

  public List<TaskNameAndStatus> getTasksAndTaskStatuses() {
    List<TaskNameAndStatus> result = new ArrayList<>();
    repositories.forEach(
        repo -> result.add(new TaskNameAndStatus(repo.getTaskName(), repo.getLabels().get(0))));
    return result;
  }

  //todo what if there are more then 1 label
  public Integer getCompletedTasks() {
    return repositories.stream()
        .mapToInt(value -> {
          List<String> labels = value.getLabels();
//          if (labels.isEmpty()) {
//            return 0;
//          }
          if (labels.get(0).equals("done")) {
            return 1;
          } else {
            return 0;
          }
        }).sum();
  }

  public static Comparator<GithubUserDto> getComparatorForLeaderboardASC() {
    return Comparator.comparingInt(GithubUserDto::getTotalPoints);
  }

  public static Comparator<GithubUserDto> getComparatorForLeaderboardDESC() {
    return Comparator.comparingInt(GithubUserDto::getTotalPoints).reversed();
  }
  
  public static Comparator<GithubUserDto> getComparatorForTaskStatusesDESC() {
    return Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed();
  }

}
