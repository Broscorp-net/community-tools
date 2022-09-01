package com.community.tools.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class GithubUserDto {
  private final String gitName;
  private final LocalDate lastCommit;
}
