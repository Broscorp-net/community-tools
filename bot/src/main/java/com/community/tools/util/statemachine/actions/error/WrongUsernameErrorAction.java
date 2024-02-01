package com.community.tools.util.statemachine.actions.error;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
@RequiredArgsConstructor
public class WrongUsernameErrorAction implements Action<State, Event> {

  private final MessageService<?> messageService;

  @Override
  public void execute(final StateContext<State, Event> context) {
    VerificationPayload payload = (VerificationPayload) context.getExtendedState()
        .getVariables()
        .get("dataPayload");
    String id = payload.getId();
    messageService.sendPrivateMessage(messageService.getUserById(id),
        Messages.GITHUB_ACCOUNT_NOT_FOUND);
  }
}
