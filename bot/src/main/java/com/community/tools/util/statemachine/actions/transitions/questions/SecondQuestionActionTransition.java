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
public class SecondQuestionActionTransition implements Transition {
  private MessageService messageService;
  private Action<State, Event> errorAction;
  private UserRepository userRepository;
  private MessageConstructor messageConstructor;

  public SecondQuestionActionTransition(@Lazy MessageService messageService,
                                        Action<State, Event> errorAction,
                                        UserRepository userRepository, MessageConstructor messageConstructor) {
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
        .source(State.FIRST_QUESTION)
        .target(State.SECOND_QUESTION)
        .event(Event.QUESTION_SECOND)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    QuestionPayload payloadFirstAnswer = (QuestionPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String id = payloadFirstAnswer.getUser();
    User stateEntity = userRepository.findByUserId(id).get();
    stateEntity.setFirstAnswerAboutRules(payloadFirstAnswer.getAnswer());
    userRepository.save(stateEntity);
    messageService.sendBlocksMessage(
        messageService.getUserById(id),
        messageConstructor.createSecondQuestion(Messages.SECOND_QUESTION));
  }
}
