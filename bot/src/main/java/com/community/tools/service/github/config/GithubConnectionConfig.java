package com.community.tools.service.github.config;

import lombok.SneakyThrows;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubConnectionConfig {

  /**
   * Returns configured GitHub API client.
   *
   * @param githubToken GitHub token
   * @return configured GitHub API client
   */
  @SneakyThrows
  @Bean
  public GitHub gitHub(@Value("${github.token}") String githubToken) {
    return new GitHubBuilder()
        .withOAuthToken(githubToken)
        .build();
  }
}
