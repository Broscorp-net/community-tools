package com.community.tools.service.github.event;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryGithubEventHandlerTest {

  @MockBean
  private UserRepository userRepository;

  @Autowired
  private RepositoryGithubEventHandler repositoryGithubEventHandler;

  @Test
  public void testHandleEvent_whenEventDoesntHaveActionKey() {
    JSONObject eventJson = new JSONObject();

    repositoryGithubEventHandler.handleEvent(eventJson);

    verify(userRepository, never()).save(any());
  }

  @Test
  public void testHandleEvent_whenEventHasWrongActionValue() {
    JSONObject eventJson = new JSONObject();
    eventJson.put("action", "random");

    repositoryGithubEventHandler.handleEvent(eventJson);

    verify(userRepository, never()).save(any());
  }

  @Test
  public void testHandleEvent_whenEventIsValid() {
    String gitLogin = "TestUser";

    User user = new User();
    user.setGitName(gitLogin);

    when(userRepository.findByGitName(gitLogin)).thenReturn(Optional.of(user));

    JSONObject eventJson = new JSONObject();
    eventJson.put("action", "created");

    String repositoryName = "intro-TestUser";
    String createdAt = "2022-09-15T15:19:25Z";
    String updatedAt = "2022-09-15T15:21:03Z";

    JSONObject repository = new JSONObject();
    eventJson.put("repository", repository);
    repository.put("name", repositoryName);
    repository.put("created_at", createdAt);
    repository.put("updated_at", updatedAt);

    JSONObject owner = new JSONObject();
    repository.put("owner", owner);
    owner.put("login", gitLogin);

    repositoryGithubEventHandler.handleEvent(eventJson);

    assertEquals(1, user.getRepositories().size());
    assertEquals("intro", user.getRepositories().get(0).getTaskName());
    assertEquals(repositoryName, user.getRepositories().get(0).getRepositoryName());
    assertEquals(TaskStatus.IN_PROGRESS, user.getRepositories().get(0).getTaskStatus());
    assertEquals(LocalDate.of(2022, 9, 15), user.getRepositories().get(0).getCreated());
    assertEquals(LocalDate.of(2022, 9, 15), user.getRepositories().get(0).getUpdated());
  }
}
