package com.community.tools.service.github.event;

import com.community.tools.exception.UserNotFoundException;
import com.community.tools.model.Repository;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
      JSONObject repositoryJson = eventJson.getJSONObject("repository");
      String repositoryName = repositoryJson.getString("name");

      String taskName = parseTaskName(repositoryName);
      if (taskName == null) {
        return;
      }

      String ownerLogin = repositoryJson.getJSONObject("owner").getString("login");
      LocalDate createdAt = LocalDate.parse(repositoryJson.getString("created_at"),
          DateTimeFormatter.ISO_DATE_TIME);
      LocalDate updatedAt = LocalDate.parse(repositoryJson.getString("updated_at"),
          DateTimeFormatter.ISO_DATE_TIME);

      Repository repository = new Repository(taskName,
          TaskStatus.IN_PROGRESS,
          repositoryName,
          createdAt,
          updatedAt);

      User user = userRepository
          .findByGitName(ownerLogin)
          .orElseThrow(() -> new UserNotFoundException(ownerLogin));

      user.addRepository(repository);
    }
  }

  private String parseTaskName(String repositoryName) {
    String taskName = null;
    for (String prefix : repositoryNamePrefixes) {
      if (repositoryName.startsWith(prefix)) {
        taskName = prefix;
        break;
      }
    }

    return taskName;
  }
}
