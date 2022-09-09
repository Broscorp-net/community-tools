package com.community.tools.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubRepositoryDto {

  private final String repositoryName;
  private final String taskName;
  private final String lastBuildStatus;
  private final List<String> labels;
  private final int points;
  private final LocalDate createdAt;
  private final LocalDate updatedAt;
}
