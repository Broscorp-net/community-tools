package com.community.tools.util.statemachine.actions.transitions.tasks;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

@WithStateMachine
public class LastTaskActionTransition implements Transition {
  private MessageService messageService;
  private Action<State, Event> errorAction;
  private Guard<State, Event> lastTaskGuard;

  public LastTaskActionTransition(@Lazy MessageService messageService,
                                  Action<State, Event> errorAction,
                                  Guard<State, Event> lastTaskGuard) {
    this.messageService = messageService;
    this.errorAction = errorAction;
    this.lastTaskGuard = lastTaskGuard;
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.CHECK_FOR_NEW_TASK)
        .target(State.CONGRATS_LAST_TASK)
        .event(Event.LAST_TASK)
        .guard(lastTaskGuard)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    messageService.sendPrivateMessage(messageService.getUserById(user), Messages.LAST_TASK);
  }
}
