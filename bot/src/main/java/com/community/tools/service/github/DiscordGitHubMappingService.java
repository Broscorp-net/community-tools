package com.community.tools.service.github;

import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageService;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordGitHubMappingService {

  private final MessageService<?> messageService;
  private final UserRepository userRepository;

  /**
   * Searches for users within provided GitHub usernames
   * and returns map of GitHub-Discord usernames.
   * @param githubUsernames List of GitHub usernames
   * @return Map of GitHub-Discord usernames
   */
  public Map<String, String> getDiscordGithubUsernames(List<String> githubUsernames) {
    return userRepository.findByGitNameIn(githubUsernames).stream()
      .collect(Collectors.toMap(User::getGitName, u -> Objects.requireNonNull(
        messageService.retrieveById(u.getUserID()))));
  }

  /**
   * Searches for a user with a provided GitHub username and returns Discord username.
   * @param githubName GitHub username
   * @return Discord username
   */
  public String getDiscordName(String githubName) {
    String userId = userRepository.findByGitName(githubName)
        .orElseThrow(() -> new NoSuchElementException("User with GitHub name = ["
            + githubName + "] was not found")).getUserID();
    return messageService.getUserById(userId);
  }

}
