package com.community.tools.util.statemachine.actions.verifications;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.model.User;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.slack.SlackHandlerService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.transitions.verifications.AddGitNameActionTransition;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ActiveProfiles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("slack")
public class AddGitNameActionTest {

  private AddGitNameActionTransition addGitNameAction;

  private StateMachineRepository repository;

  private StateContext<State, Event> stateContext;

  private GitHubConnectService gitHubConnectService;

  private GitHubService gitHubService;

  private MessageService messageService;

  private MessageConstructor messageConstructor;

  private SlackHandlerService slackHandlerService;

  private StateMachine<State, Event> machine;

  private ExtendedState extendedState;

  private GHUser user;

  private GHTeam team;

  private GHRepository ghRepository;

  private final String getFirstTask = "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Hurray! Your nick is available. Nice to meet you :smile:\n\nThis is your first <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/checkstyle|TASK>. gl\"}}]";
  private final String errorWithAddingGitName = "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Something went wrong with adding to the team. Please, contact *<https://broscorp-community.slack.com/archives/D01QZ9U2GH5|Liliya Stepanovna>*\"}}]";

  /**
   * This method init fields in the AddGitNameAction.
   */
  @BeforeAll
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Used for refresh mocks.
   */
  @BeforeEach
  public void refreshMocks() {
    this.repository = Mockito.mock(StateMachineRepository.class);
    this.stateContext = Mockito.mock(StateContext.class);
    this.gitHubConnectService = Mockito.mock(GitHubConnectService.class);
    this.gitHubService = Mockito.mock(GitHubService.class);
    this.messageService = Mockito.mock(MessageService.class);
    this.messageConstructor = Mockito.mock(MessageConstructor.class);
    this.slackHandlerService = Mockito.mock(SlackHandlerService.class);
    this.machine = Mockito.mock(StateMachine.class);
    this.extendedState = Mockito.mock(ExtendedState.class);
    this.user = Mockito.mock(GHUser.class);
    this.team = Mockito.mock(GHTeam.class);
    this.ghRepository = Mockito.mock(GHRepository.class);

    this.addGitNameAction = new AddGitNameActionTransition(null, "test_3",
      repository, gitHubConnectService, gitHubService, messageService, messageConstructor);

  }

  @Test
  public void executeTest() throws Exception {
    Map<Object, Object> mockData = new HashMap<>();

    Payload payload = new VerificationPayload("U0191K2V20K", "likeRewca");
    mockData.put("dataPayload", payload);

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    final User entity = new User();

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doNothing().when(team).add(user);
    when(messageConstructor.createErrorWithAddingGitNameMessage(errorWithAddingGitName))
      .thenReturn(errorWithAddingGitName);
    when(messageConstructor.createGetFirstTaskMessage(anyString(), anyString(), anyString()))
      .thenReturn(getFirstTask);
    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");

    addGitNameAction.execute(stateContext);

    verify(stateContext, times(2)).getExtendedState();
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(1)).getGitHubRepository();
    verify(messageService, times(2)).getUserById("U0191K2V20K");
    verify(messageService, times(1)).sendMessageToConversation(anyString(), anyString());
    verify(messageService, times(1))
            .sendBlocksMessage("Горб Юра",
        getFirstTask);
  }


  @SneakyThrows
  @Test
  public void shouldGetExceptionWhenAddingToRole() throws IOException {
    Map<Object, Object> mockData = new HashMap<>();

    Payload payload = new VerificationPayload("U0191K2V20K", "likeRewca");
    mockData.put("dataPayload", payload);

    Set<GHTeam> mockSet = new HashSet<>();
    mockSet.add(team);

    final User entity = new User();

    when(messageConstructor.createGetFirstTaskMessage(anyString(), anyString(), anyString()))
      .thenReturn(getFirstTask);
    when(messageConstructor.createErrorWithAddingGitNameMessage(anyString()))
      .thenReturn(errorWithAddingGitName);

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));
    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(gitHubConnectService.getGitHubRepository()).thenReturn(ghRepository);
    when(ghRepository.getTeams()).thenReturn(mockSet);
    when(team.getName()).thenReturn("trainees");
    doThrow(IOException.class).when(team).add(user);
    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");

    addGitNameAction.execute(stateContext);

    verify(stateContext, times(2)).getExtendedState();
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
    verify(gitHubConnectService, times(1)).getGitHubRepository();
    verify(messageService, times(3)).getUserById("U0191K2V20K");
    verify(messageService, times(1))
            .sendBlocksMessage("Горб Юра",
        errorWithAddingGitName);
    verify(messageService, times(1)).sendMessageToConversation(anyString(), anyString());

    verify(messageService, times(1))
            .sendBlocksMessage("Горб Юра",
        getFirstTask);

  }
}
