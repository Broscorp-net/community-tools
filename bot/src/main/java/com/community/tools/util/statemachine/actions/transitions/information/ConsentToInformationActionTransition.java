package com.community.tools.util.statemachine.actions.transitions.information;

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
public class ConsentToInformationActionTransition implements Transition {
  private final Action<State, Event> errorAction;
  private final UserRepository userRepository;
  private final MessageService messageService;
  private final MessageConstructor messageConstructor;

  public ConsentToInformationActionTransition(Action<State, Event> errorAction,
                                              UserRepository userRepository,
                                              @Lazy MessageService messageService,
                                              MessageConstructor messageConstructor) {
    this.errorAction = errorAction;
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.messageConstructor = messageConstructor;
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    QuestionPayload payloadThirdAnswer = (QuestionPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String id = payloadThirdAnswer.getUser();
    User stateEntity = userRepository.findByUserId(id).get();
    stateEntity.setThirdAnswerAboutRules(payloadThirdAnswer.getAnswer());
    userRepository.save(stateEntity);
    messageService.sendBlocksMessage(
        messageService.getUserById(id),
        messageConstructor.createMessageAboutSeveralInfoChannel(Messages.INFO_CHANNEL_MESSAGES));
    messageService.sendBlocksMessage(messageService.getUserById(id),
        messageConstructor.createAddGitNameMessage(Messages.ADD_GIT_NAME));
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.THIRD_QUESTION)
        .target(State.AGREED_LICENSE)
        .event(Event.CONSENT_TO_INFORMATION)
        .action(this, errorAction);
  }
}
