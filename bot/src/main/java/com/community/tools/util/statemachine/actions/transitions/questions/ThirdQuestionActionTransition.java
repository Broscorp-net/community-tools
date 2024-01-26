package com.community.tools.util.statemachine.actions.transitions.questions;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.QuestionPayload;
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
public class ThirdQuestionActionTransition implements Transition {
  private final MessageService messageService;
  private final Action<State, Event> errorAction;
  private final UserRepository userRepository;
  private final MessageConstructor messageConstructor;

  public ThirdQuestionActionTransition(@Lazy MessageService messageService,
                                       Action<State, Event> errorAction,
                                       UserRepository userRepository,
                                       MessageConstructor messageConstructor) {
    this.messageService = messageService;
    this.errorAction = errorAction;
    this.userRepository = userRepository;
    this.messageConstructor = messageConstructor;
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.SECOND_QUESTION)
        .target(State.THIRD_QUESTION)
        .event(Event.QUESTION_THIRD)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    QuestionPayload payloadSecondAnswer = (QuestionPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String id = payloadSecondAnswer.getUser();
    User stateEntity = userRepository.findByUserId(id).get();
    stateEntity.setSecondAnswerAboutRules(payloadSecondAnswer.getAnswer());
    userRepository.save(stateEntity);
    messageService.sendBlocksMessage(
        messageService.getUserById(id),
        messageConstructor.createThirdQuestion(Messages.THIRD_QUESTION));
  }
}
