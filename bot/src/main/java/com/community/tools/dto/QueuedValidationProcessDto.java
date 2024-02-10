package com.community.tools.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHPullRequest;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueuedValidationProcessDto {
  private String prompt;
  private String traineeGitName;
  private GHPullRequest pullRequest;
  private Map<String, Integer> fileCodeLines;
}
