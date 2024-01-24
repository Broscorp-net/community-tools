package com.community.tools.util.statemachine.actions.transitions.verifications;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHUser;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Slf4j
@WithStateMachine
@RequiredArgsConstructor
public class VerificationLoginActionTransition implements Transition {

  private final Action<State, Event> wrongUsernameErrorAction;
  private final GitHubService gitHubService;
  private final MessageService<?> messageService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.NEW_USER)
        .target(State.CHECK_LOGIN)
        .event(Event.LOGIN_CONFIRMATION)
        .action(this, wrongUsernameErrorAction);
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
      log.error("GitHub account with username {} was not found", nickname, e);
      throw new RuntimeException(e);
    }
  }
}
