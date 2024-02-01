package com.community.tools.util.statemachine.actions.transitions.verifications;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
@RequiredArgsConstructor
public class DidNotPassVerificationGitLoginTrans implements Transition {

  private final Action<State, Event> errorAction;
  private final MessageService<?> messageService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.CHECK_LOGIN)
        .target(State.NEW_USER)
        .event(Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    VerificationPayload payload = (VerificationPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String user = payload.getId();
    messageService.sendPrivateMessage(messageService.getUserById(user),
        Messages.ANSWERED_NO_DURING_VERIFICATION);
  }
}
