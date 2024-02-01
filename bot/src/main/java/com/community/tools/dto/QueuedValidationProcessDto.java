package com.community.tools.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueuedValidationProcessDto {
  private String prompt;
  private String committer;
  private GHPullRequest pullRequest;
  private GHPullRequestCommitDetail commitDetail;
  private Map<String, Integer> fileCodeLines;
}
