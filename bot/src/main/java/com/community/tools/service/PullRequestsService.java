package com.community.tools.service;

import com.community.tools.service.github.GitHubConnectService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PullRequestsService {

  private final GitHubConnectService gitHubConnectService;

  /**
   * Get all pull requests.
   * @return all pull requests
   */
  public List<GHPullRequest> getPullRequests() {
    List<GHPullRequest> pullRequests;
    try {
      pullRequests = gitHubConnectService.getGitHubRepository().getPullRequests(GHIssueState.ALL);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    return pullRequests;
  }

  /**
   * Get last label from pull request.
   * @param ghPullRequest pull request
   * @return last label or pull request string
   */
  public String getLastLabel(GHPullRequest ghPullRequest) {
    String lastLabel;
    try {
      lastLabel = ghPullRequest.getLabels().isEmpty() ? "pull request" :
          ghPullRequest.getLabels().stream().findFirst().get().getName();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    return lastLabel;
  }
}