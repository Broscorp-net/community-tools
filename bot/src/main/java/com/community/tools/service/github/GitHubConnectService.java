package com.community.tools.service.github;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubConnectService {

  @Value("${github.repository}")
  private String nameRepository;

  private final GitHub gitHub;

  /**
   * Get GitHub connection.
   *
   * @return GitHub
   */
  public GitHub getGitHubConnection() {
    return gitHub;
  }

  /**
   * Get GitHub repository.
   *
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
