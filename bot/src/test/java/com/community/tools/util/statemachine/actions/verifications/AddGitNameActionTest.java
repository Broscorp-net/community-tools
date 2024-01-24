package com.community.tools.util.statemachine.actions.verifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.ClassroomService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.slack.SlackHandlerService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.transitions.verifications.AddGitNameActionTransition;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ActiveProfiles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("slack")
public class AddGitNameActionTest {

  private final String getFirstTask = "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Hurray! Your nick is available. Nice to meet you :smile:\n\nThis is your first <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/checkstyle|TASK>. gl\"}}]";
  private final String errorWithAddingGitName = "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Something went wrong with adding to the team. Please, contact *<https://broscorp-community.slack.com/archives/D01QZ9U2GH5|Liliya Stepanovna>*\"}}]";
  private AddGitNameActionTransition addGitNameAction;
  private UserRepository repository;
  private StateContext<State, Event> stateContext;
  private ClassroomService classroomService;
  private MessageService messageService;
  private MessageConstructor messageConstructor;
  private SlackHandlerService slackHandlerService;
  private StateMachine<State, Event> machine;
  private ExtendedState extendedState;

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
    this.repository = Mockito.mock(UserRepository.class);
    this.stateContext = Mockito.mock(StateContext.class);
    this.classroomService = Mockito.mock(ClassroomService.class);
    this.messageService = Mockito.mock(MessageService.class);
    this.messageConstructor = Mockito.mock(MessageConstructor.class);
    this.slackHandlerService = Mockito.mock(SlackHandlerService.class);
    this.machine = Mockito.mock(StateMachine.class);
    this.extendedState = Mockito.mock(ExtendedState.class);

    this.addGitNameAction = new AddGitNameActionTransition(null,
        repository, classroomService, messageService);
  }

  @Test
  public void executeTest() throws Exception {
    Map<Object, Object> mockData = new HashMap<>();

    String userId = "U0191K2V20K";
    VerificationPayload payload = new VerificationPayload(userId, "likeRewca");
    mockData.put("dataPayload", payload);

    User entity = new User();
    String guildId = "213876";
    entity.setGuildId(guildId);

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(repository.findByUserID(userId)).thenReturn(Optional.of(entity));

    when(messageConstructor.createErrorWithAddingGitNameMessage(errorWithAddingGitName))
        .thenReturn(errorWithAddingGitName);
    when(messageConstructor.createGetFirstTaskMessage(anyString(), anyString(), anyString()))
        .thenReturn(getFirstTask);
    when(messageService.getUserById(userId)).thenReturn("Горб Юра");

    addGitNameAction.execute(stateContext);

    verify(stateContext, times(2)).getExtendedState();
    verify(classroomService, times(1))
        .addUserToTraineesTeam(payload.getGitNick());
    verify(messageService, times(1)).getUserById(userId);
    verify(messageService, times(1))
        .removeRole(eq(guildId), eq(userId), any());
  }


  @SneakyThrows
  @Test
  public void shouldGetExceptionWhenAddingToRole() {
    Map<Object, Object> mockData = new HashMap<>();

    Payload payload = new VerificationPayload("U0191K2V20K", "likeRewca");
    mockData.put("dataPayload", payload);

    User entity = new User();

    when(messageConstructor.createGetFirstTaskMessage(anyString(), anyString(), anyString()))
        .thenReturn(getFirstTask);
    when(messageConstructor.createErrorWithAddingGitNameMessage(anyString()))
        .thenReturn(errorWithAddingGitName);

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(repository.findByUserID("U0191K2V20K")).thenReturn(Optional.of(entity));
    doThrow(RuntimeException.class).when(classroomService).addUserToTraineesTeam("likeRewca");
    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");

    addGitNameAction.execute(stateContext);

    verify(stateContext, times(2)).getExtendedState();
    verify(classroomService, times(1)).addUserToTraineesTeam("likeRewca");
    verify(messageService, times(1)).getUserById("U0191K2V20K");

  }
}
