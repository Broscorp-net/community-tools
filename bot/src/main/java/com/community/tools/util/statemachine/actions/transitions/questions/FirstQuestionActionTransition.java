package com.community.tools.util.statemachine.actions.transitions.questions;

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
public class FirstQuestionActionTransition implements Transition {
  private final MessageService messageService;
  private final MessageConstructor messageConstructor;
  private final Action<State, Event> errorAction;

  public FirstQuestionActionTransition(@Lazy MessageService messageService,
                                       MessageConstructor messageConstructor,
                                       Action<State, Event> errorAction) {
    this.messageService = messageService;
    this.messageConstructor = messageConstructor;
    this.errorAction = errorAction;
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    SimplePayload payload = (SimplePayload) stateContext.getExtendedState().getVariables()
        .get("dataPayload");
    String id = payload.getId();
    messageService.sendBlocksMessage(
        messageService.getUserById(id),
        messageConstructor.createFirstQuestion(Messages.FIRST_QUESTION));
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.RULES)
        .target(State.FIRST_QUESTION)
        .event(Event.QUESTION_FIRST)
        .action(this, errorAction);
  }
}
