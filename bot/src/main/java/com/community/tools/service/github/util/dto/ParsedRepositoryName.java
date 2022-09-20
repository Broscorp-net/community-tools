package com.community.tools.service.github.util.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParsedRepositoryName {
  private String creatorGitName;
  private String taskName;
}
