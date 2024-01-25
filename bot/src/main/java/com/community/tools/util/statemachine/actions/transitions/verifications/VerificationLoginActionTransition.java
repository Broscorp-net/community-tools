package com.community.tools.util.statemachine.actions.transitions.verifications;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.io.IOException;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class VerificationLoginActionTransition implements Transition {
  private Action<State, Event> errorAction;
  private GitHubService gitHubService;
  private MessageService messageService;

  public VerificationLoginActionTransition(Action<State, Event> errorAction,
                                           GitHubService gitHubService,
                                           @Lazy MessageService messageService) {
    this.errorAction = errorAction;
    this.gitHubService = gitHubService;
    this.messageService = messageService;
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
      messageService.sendPrivateMessage(messageService.getUserById(id),
          Messages.ASK_ABOUT_PROFILE + "\n" + userGitLogin.getHtmlUrl().toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
