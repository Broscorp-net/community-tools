package com.community.tools.util.statemachine.actions.transitions.tasks;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class GetTheFirstTaskActionTransition implements Transition {
  private MessageService messageService;
  private Action<State, Event> errorAction;
  private MessageConstructor messageConstructor;

  public GetTheFirstTaskActionTransition(@Lazy MessageService messageService,
                                         Action<State, Event> errorAction,
                                         MessageConstructor messageConstructor) {
    this.messageService = messageService;
    this.errorAction = errorAction;
    this.messageConstructor = messageConstructor;
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(State.ADDED_GIT)
        .target(State.GOT_THE_TASK)
        .event(Event.GET_THE_FIRST_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    SimplePayload payload =
        (SimplePayload) stateContext.getExtendedState().getVariables().get("dataPayload");
    String user = payload.getId();
    messageService.sendBlocksMessage(
        messageService.getUserById(user),
        messageConstructor.createGetFirstTaskMessage(
            Messages.CONGRATS_AVAILABLE_NICK, Messages.GET_FIRST_TASK, Messages.LINK_FIRST_TASK));
  }
}
