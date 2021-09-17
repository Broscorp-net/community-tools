package com.community.tools.util.statemachine.actions.transitions.tasks;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.EstimatePayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import com.community.tools.util.statemachine.actions.error.ErrorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ConfirmEstimateTaskActionTransition implements Transition {

  @Autowired
  private MessageService messageService;

  @Autowired
  ErrorAction errorAction;

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(State.ESTIMATE_THE_TASK)
        .target(State.GOT_THE_TASK)
        .event(Event.CONFIRM_ESTIMATE)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    EstimatePayload payload = (EstimatePayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String user = payload.getId();

    messageService.sendPrivateMessage(messageService.getUserById(user), Messages.CONFIRM_ESTIMATE);
    stateContext.getExtendedState().getVariables().put("value", payload.getValue());
  }
}
