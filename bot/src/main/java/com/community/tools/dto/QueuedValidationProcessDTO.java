package com.community.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueuedValidationProcessDTO {
    private String prompt;
    private GHPullRequest pullRequest;
    private GHPullRequestCommitDetail commitDetail;
    private Map<String, Integer> fileCodeLines;
}
