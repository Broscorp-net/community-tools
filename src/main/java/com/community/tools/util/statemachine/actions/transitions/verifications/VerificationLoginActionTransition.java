package com.community.tools.util.statemachine.actions.transitions.verifications;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.io.IOException;
import java.util.Map;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class VerificationLoginActionTransition implements Transition {

  @Autowired
  private Action<State, Event> errorAction;
  @Value("${askAboutProfile}")
  private String askAboutProfile;

  @Autowired
  private GitHubService gitHubService;

  @Autowired
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.AGREED_LICENSE)
        .target(State.CHECK_LOGIN)
        .event(Event.LOGIN_CONFIRMATION)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    VerificationPayload payload = (VerificationPayload) stateContext.getExtendedState()
        .getVariables()
        .get("dataPayload");
    String id = payload.getId();
    String nickname = payload.getGitNick();
    try {
      GHUser userGitLogin = gitHubService.getUserByLoginInGitHub(nickname);
      getMessageService().sendPrivateMessage(getMessageService().getUserById(id),
          Messages.ASK_ABOUT_PROFILE + "\n" + userGitLogin.getHtmlUrl().toString());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
