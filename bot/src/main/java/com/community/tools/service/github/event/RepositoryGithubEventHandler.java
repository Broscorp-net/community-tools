package com.community.tools.service.github.event;

import com.community.tools.exception.UserNotFoundException;
import com.community.tools.model.Repository;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.github.util.RepositoryNameService;
import com.community.tools.service.github.util.dto.ParsedRepositoryName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
public class RepositoryGithubEventHandler implements GithubEventHandler {

  private final UserRepository userRepository;
  private final RepositoryNameService repositoryNameService;

  @Transactional
  @Override
  public void handleEvent(JSONObject eventJson) {
    if (eventJson.has("action") && eventJson.getString("action").equals("created")) {
      JSONObject repositoryJson = eventJson.getJSONObject("repository");
      String repositoryName = repositoryJson.getString("name");

      if (!repositoryNameService.isPrefixedWithTaskName(repositoryName)) {
        return;
      }

      ParsedRepositoryName parsedRepositoryName = repositoryNameService.parseRepositoryName(
          repositoryName);

      String taskName = parsedRepositoryName.getTaskName();
      String creatorGitName = parsedRepositoryName.getCreatorGitName();
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
          .findByGitName(creatorGitName)
          .orElseThrow(() -> new UserNotFoundException(creatorGitName));

      user.addRepository(repository);
    }
  }
}
