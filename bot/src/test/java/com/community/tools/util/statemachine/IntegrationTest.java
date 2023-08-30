package com.community.tools.util.statemachine;

import static com.community.tools.util.statemachine.State.ADDED_GIT;
import static com.community.tools.util.statemachine.State.AGREED_LICENSE;
import static com.community.tools.util.statemachine.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachine.State.CHECK_LOGIN;
import static com.community.tools.util.statemachine.State.ESTIMATE_THE_TASK;
import static com.community.tools.util.statemachine.State.FIRST_QUESTION;
import static com.community.tools.util.statemachine.State.GETTING_PULL_REQUEST;
import static com.community.tools.util.statemachine.State.GOT_THE_TASK;
import static com.community.tools.util.statemachine.State.NEW_USER;
import static com.community.tools.util.statemachine.State.RULES;
import static com.community.tools.util.statemachine.State.SECOND_QUESTION;
import static com.community.tools.util.statemachine.State.THIRD_QUESTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.discord.DiscordConfig;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.StatisticsServiceImpl;
import com.community.tools.service.github.ClassroomService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.EstimatePayload;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.service.payload.VerificationPayload;
import java.net.URL;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
@ActiveProfiles("slack")
class IntegrationTest {

  private static final String USER_ID = "U01QY6GRZ0X";
  private static final String USER_NAME = "Some User";
  private static final Integer VALUE_FOR_ESTIMATE = 1;
  @Value("${estimateTheTask}")
  String estimateTheTask;
  @Value("${welcomeChannelMessage}")
  private String welcomeChanelMessage;
  @Value("${firstQuestion}")
  private String firstQuestion;
  @Value("${secondQuestion}")
  private String secondQuestion;
  @Value("${thirdQuestion}")
  private String thirdQuestion;
  @Value("${messageAboutSeveralInfoChannel}")
  private String messageAboutSeveralInfoChannel;
  @Value("${addGitName}")
  private String addGitName;
  @Value("${askAboutProfile}")
  private String askAboutProfile;
  @Value("${getFirstTask}")
  private String getFirstTask;
  @Value("${answeredNoDuringVerification}")
  private String answeredNoDuringVerification;
  @Value("${lastTask}")
  private String lastTask;
  @Value("${generalInformationChannel}")
  private String channel;
  @Autowired
  private StateMachineService stateMachineService;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private MessageService messageService;

  @MockBean
  private GitHubService gitHubService;

  @MockBean
  private GitHubConnectService gitHubConnectService;

  @MockBean
  private DiscordConfig discordConfig;

  @MockBean
  private ClassroomService classroomService;

  @MockBean
  private StatisticsServiceImpl statisticsServiceImpl;

  @Mock
  private GHUser user;

  @Mock
  private GHTeam team;

  @Mock
  private GHRepository ghRepository;

  @Captor
  private ArgumentCaptor<String> firstArg;

  @Captor
  private ArgumentCaptor<String> secondArg;

  @Captor
  private ArgumentCaptor<GHUser> ghUserCaptor;

  private StateMachine<State, Event> machine;
  private String userForQuestion;

  @BeforeEach
  void setUp() throws Exception {
    if (machine == null) {
      User stateEntity = new User();
      stateEntity.setUserID(USER_ID);
      userRepository.save(stateEntity);
      machine = stateMachineService.restoreMachine(USER_ID);
      machine.getExtendedState().getVariables()
          .put("gitNick", USER_NAME);
      machine.getExtendedState().getVariables()
          .put("id", USER_ID);
      userForQuestion = machine
          .getExtendedState().getVariables().get("id").toString();
    }
  }

  @SneakyThrows
  @Test
  void welcomeActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(NEW_USER,
            null, null, null)));
    Payload payload = new SimplePayload(USER_ID);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.GET_RULES);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(welcomeChanelMessage, secondArg.getValue());
  }


  @SneakyThrows
  @Test
  void firstQuestionActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(RULES,
            null, null, null)));
    Payload payload = new SimplePayload(USER_ID);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.QUESTION_FIRST);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(firstQuestion, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void secondQuestionActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(FIRST_QUESTION,
            null, null, null)));
    Payload payload = new QuestionPayload(USER_ID, "First", userForQuestion);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.QUESTION_SECOND);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(secondQuestion, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void thirdQuestionActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(SECOND_QUESTION,
            null, null, null)));
    Payload payload = new QuestionPayload(USER_ID, "Second", userForQuestion);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.QUESTION_THIRD);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(thirdQuestion, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void channelInformationActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(THIRD_QUESTION,
            null, null, null)));
    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);
    Payload payload = new QuestionPayload(USER_ID, "Third", userForQuestion);

    stateMachineService.doAction(machine, payload, Event.CONSENT_TO_INFORMATION);

    verify(messageService, times(2)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(2)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(addGitName, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void verificationLoginActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(AGREED_LICENSE,
            null, null, null)));
    VerificationPayload payload = new VerificationPayload(USER_ID, USER_NAME);

    when(gitHubService.getUserByLoginInGitHub(payload.getGitNick())).thenReturn(user);
    URL url = new URL("http://www.some.com/");
    when(user.getHtmlUrl()).thenReturn(url);
    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.LOGIN_CONFIRMATION);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    ;
    verify(messageService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(askAboutProfile + "\n" + url, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void addGitNameActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_LOGIN,
            null, null, null)));
    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);
    VerificationPayload payload = new VerificationPayload(USER_ID, USER_NAME);

    stateMachineService.doAction(machine, payload, Event.ADD_GIT_NAME_AND_FIRST_TASK);

    verify(classroomService, times(1)).addUserToTraineesTeam(firstArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    verify(messageService, times(2)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendMessageToConversation(firstArg.capture(), anyString());
    assertEquals(channel, firstArg.getValue());
  }

  @SneakyThrows
  @Test
  void getTheFirstTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(ADDED_GIT,
            null, null, null)));
    Payload payload = new SimplePayload(USER_ID);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.GET_THE_FIRST_TASK);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(getFirstTask, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void didNotPassVerificationLoginTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_LOGIN,
            null, null, null)));
    VerificationPayload payload = new VerificationPayload(USER_ID, USER_NAME);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(answeredNoDuringVerification, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void confirmEstimateTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(ESTIMATE_THE_TASK,
            null, null, null)));
    Payload payload = new EstimatePayload(USER_ID, VALUE_FOR_ESTIMATE);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.CONFIRM_ESTIMATE);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(Messages.CONFIRM_ESTIMATE, secondArg.getValue());
    assertEquals(VALUE_FOR_ESTIMATE, machine.getExtendedState().getVariables().get("value"));
  }

  @SneakyThrows
  @Test
  void resendingEstimateTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(GOT_THE_TASK,
            null, null, null)));
    Payload payload = new SimplePayload(USER_ID);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.RESENDING_ESTIMATE_TASK);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(estimateTheTask, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void sendEstimateTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(GETTING_PULL_REQUEST,
            null, null, null)));
    Payload payload = new SimplePayload(USER_ID);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    stateMachineService.doAction(machine, payload, Event.SEND_ESTIMATE_TASK);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(estimateTheTask, secondArg.getValue());
  }

  @SneakyThrows
  @Test
  void getTheNewTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(GOT_THE_TASK,
            null, null, null)));
    machine.getExtendedState().getVariables()
        .put("taskNumber", 0);

    String taskMessage =
        "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": "
            + "\"Here is your next "
            + "<https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/"
            + "checkstyle" + "|TASK>.\"}}]";
    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    machine.sendEvent(Event.GET_THE_NEW_TASK);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendBlocksMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
  }

  @SneakyThrows
  @Test
  void changeTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_FOR_NEW_TASK,
            null, null, null)));
    machine.getExtendedState().getVariables()
        .put("taskNumber", 0);

    machine.sendEvent(Event.CHANGE_TASK);

    assertEquals(1, machine.getExtendedState().getVariables().get("taskNumber"));
  }

  @SneakyThrows
  @Test
  void lastTaskActionTest() {
    machine.getStateMachineAccessor().doWithAllRegions(access -> access
        .resetStateMachine(new DefaultStateMachineContext<>(CHECK_FOR_NEW_TASK,
            null, null, null)));
    machine.getExtendedState().getVariables()
        .put("taskNumber", 14);

    when(messageService.getUserById(USER_ID)).thenReturn(USER_NAME);

    machine.sendEvent(Event.LAST_TASK);

    verify(messageService, times(1)).getUserById(firstArg.capture());
    assertEquals(USER_ID, firstArg.getValue());
    verify(messageService, times(1)).sendPrivateMessage(firstArg.capture(), secondArg.capture());
    assertEquals(USER_NAME, firstArg.getValue());
    assertEquals(lastTask, secondArg.getValue());
  }
}
