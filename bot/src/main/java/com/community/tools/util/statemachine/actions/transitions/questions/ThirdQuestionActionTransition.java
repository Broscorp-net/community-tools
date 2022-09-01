package com.community.tools.util.statemachine.actions.transitions.questions;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import com.community.tools.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ThirdQuestionActionTransition implements Transition {

  @Autowired
  private MessageService messageService;

  @Autowired
  private Action<State, Event> errorAction;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MessageConstructor messageConstructor;

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
    User stateEntity = userRepository.findByUserID(id).get();
    stateEntity.setSecondAnswerAboutRules(payloadSecondAnswer.getAnswer());
    userRepository.save(stateEntity);
    messageService.sendBlocksMessage(
        messageService.getUserById(id),
        messageConstructor.createThirdQuestion(Messages.THIRD_QUESTION));
  }
}
