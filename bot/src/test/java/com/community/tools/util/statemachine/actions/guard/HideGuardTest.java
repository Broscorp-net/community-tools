package com.community.tools.util.statemachine.actions.guard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.slack.SlackHandlerService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "/application-test.properties")
public class HideGuardTest {

  @Autowired
  private HideGuard hideGuard;
  @MockBean
  private SlackHandlerService slackHandlerService;
  @MockBean
  private StateMachineRepository repository;
  @MockBean
  private StateContext<State, Event> stateContext;
  @MockBean
  private GitHubConnectService gitHubConnectService;
  @MockBean
  private GitHubService gitHubService;
  @MockBean
  private MessageService messageService;
  @Mock
  private ExtendedState extendedState;
  @Mock
  private GHUser user;

  /**
   * This method init fields in the HideGuard.
   * @throws Exception Exception
   */
  @Before
  public void setUp() throws Exception {
    Field messageService = HideGuard.class.getDeclaredField("messageService");
    messageService.setAccessible(true);
    messageService.set(hideGuard, messageService);

    Field repoService = HideGuard.class.getDeclaredField("gitHubService");
    repoService.setAccessible(true);
    repoService.set(hideGuard, gitHubService);
  }

  @Test
  public void checkingIfThereIsALoginInTheGithubAndReturnTrue()
          throws IOException, SlackApiException {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenReturn(user);
    when(user.getLogin()).thenReturn("likeRewca");

    assertTrue(hideGuard.evaluate(stateContext));

    verify(stateContext, times(2)).getExtendedState();
    verify(messageService, times(1)).getUserById("U0191K2V20K");
    verify(messageService, times(1))
            .sendPrivateMessage("Горб Юра", "Okay! Let me check your nick, "
                    + mockData.get("gitNick"));
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
  }

  @SneakyThrows
  @Test
  public void checkWhenThereIsNotLoginInTheGithub() throws IOException {
    Map<Object, Object> mockData = new HashMap<>();
    mockData.put("id", "U0191K2V20K");
    mockData.put("gitNick", "likeRewca");

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);

    when(messageService.getUserById("U0191K2V20K")).thenReturn("Горб Юра");

    when(gitHubService.getUserByLoginInGitHub("likeRewca")).thenThrow(IOException.class);

    assertFalse(hideGuard.evaluate(stateContext));

    verify(stateContext, times(2)).getExtendedState();
    verify(messageService, times(2)).getUserById("U0191K2V20K");
    verify(messageService, times(1))
            .sendPrivateMessage("Горб Юра", "Okay! Let me check your nick, "
                    + mockData.get("gitNick"));
    verify(messageService, times(1)).sendPrivateMessage("Горб Юра",
            "Sry but looks like you are not registered on Github :worried:");
    verify(gitHubService, times(1)).getUserByLoginInGitHub("likeRewca");
  }
}