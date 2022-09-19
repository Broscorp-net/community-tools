package com.community.tools.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubRepositoryDto {

  private final String repositoryName;
  private final String taskName;
  private final Optional<String> lastBuildStatus;
  private final Optional<List<String>> labels;
  private final Optional<Integer> points;
  private final Optional<LocalDate> createdAt;
  private final LocalDate updatedAt;

}
