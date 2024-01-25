package com.community.tools.util.statemachine.actions.transitions.tasks;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import com.community.tools.util.statemachine.actions.error.ErrorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ResendingEstimateActionTask implements Transition {
  private ErrorAction errorAction;
  private MessageService messageService;
  private MessageConstructor messageConstructor;

  public ResendingEstimateActionTask(ErrorAction errorAction,
                                     @Lazy MessageService messageService,
                                     MessageConstructor messageConstructor) {
    this.errorAction = errorAction;
    this.messageService = messageService;
    this.messageConstructor = messageConstructor;
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(State.GOT_THE_TASK)
        .target(State.ESTIMATE_THE_TASK)
        .event(Event.RESENDING_ESTIMATE_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    SimplePayload payload =
        (SimplePayload) stateContext.getExtendedState().getVariables().get("dataPayload");
    String user = payload.getId();

    messageService.sendBlocksMessage(
        messageService.getUserById(user),
        messageConstructor.createEstimateTheTaskMessage(
            Messages.ESTIMATE_HEADER, Messages.ESTIMATE_QUESTIONS, Messages.ESTIMATE_FOOTER));
  }
}
