package com.community.tools.service.github.event;

import com.community.tools.model.Repository;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RepositoryGithubEventHandler implements GithubEventHandler {

  private final UserRepository userRepository;
  private final Set<String> repositoryNamePrefixes;

  @Autowired
  public RepositoryGithubEventHandler(UserRepository userRepository,
      @Value("${github.task-repository-names.prefixes}") String[] repositoryNamePrefixes) {
    this.userRepository = userRepository;
    this.repositoryNamePrefixes = new HashSet<>(Arrays.asList(repositoryNamePrefixes));
  }

  @Transactional
  @Override
  public void handleEvent(JSONObject eventJson) {
    if (eventJson.has("action") && eventJson.getString("action").equals("created")) {
      String repositoryName = eventJson.getString("name");

      String matchingPrefix = null;
      for (String prefix : repositoryNamePrefixes) {
        if (repositoryName.startsWith(prefix)) {
          matchingPrefix = prefix;
        }
      }

      if (matchingPrefix == null) {
        return;
      }

      String taskName = repositoryName.replace(matchingPrefix + "-", "");
      String ownerLogin = eventJson.getJSONObject("owner").getString("login");
      LocalDate createdAt = LocalDate.parse(eventJson.getString("created_at"));
      LocalDate updatedAt = LocalDate.parse(eventJson.getString("updated_at"));

      Repository repository = new Repository(taskName,
          TaskStatus.IN_PROGRESS,
          repositoryName,
          createdAt,
          updatedAt);

      User user = userRepository
          .findByGitName(ownerLogin)
          .orElseThrow(() -> new RuntimeException(
              String.format("user with git username '%s' not found.", ownerLogin)));

      user.addRepository(repository);
    }
  }
}
