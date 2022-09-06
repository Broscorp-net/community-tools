package com.community.tools.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@Builder
public class GithubUserDto {

  private final String gitName;
  private final LocalDate lastCommit;
  private List<GithubRepositoryDto> repositories;
}
