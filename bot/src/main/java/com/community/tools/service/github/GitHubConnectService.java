package com.community.tools.service.github;

import java.io.IOException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubConnectService {

  @Value("${github.token}")
  private String token;

  @Value("${github.repository}")
  private String nameRepository;

  /**
   * Get GitHub connection.
   * @return GitHub
   */
  public GitHub getGitHubConnection() {
    GitHub gitHub;
    try {
      gitHub = GitHub.connectUsingOAuth(token);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return gitHub;
  }

  /**
   * Get GitHub repository.
   * @return GHRepository
   */
  public GHRepository getGitHubRepository() {
    return getGitHubRepositoryByName(nameRepository);
  }

  /**
   * Method to get GitHub repository by its name.
   *
   * @param repositoryName name of the desired repository
   * @return GHRepository
   */
  public GHRepository getGitHubRepositoryByName(String repositoryName) {
    try {
      return getGitHubConnection().getRepository(repositoryName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
