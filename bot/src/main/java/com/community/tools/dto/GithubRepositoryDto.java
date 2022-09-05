package com.community.tools.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubRepositoryDto {

  private final String repositoryName;
  private final String taskName;
  private final String lastBuildStatus;
  private final Set<String> labels;
  private final LocalDate createdAt;
  private final LocalDate updatedAt;
}
