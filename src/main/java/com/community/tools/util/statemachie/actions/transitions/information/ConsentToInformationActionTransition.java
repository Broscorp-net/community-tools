package com.community.tools.util.statemachie.actions.transitions.information;

import static com.community.tools.util.statemachie.Event.CONSENT_TO_INFORMATION;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.THIRD_QUESTION;

import com.community.tools.model.User;
import com.community.tools.service.MessageService;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.actions.Transition;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class ConsentToInformationActionTransition implements Transition {

  @Value("${messageAboutSeveralInfoChannel}")
  private String messageAboutSeveralInfoChannel;

  @Value("${addGitName}")
  private String addGitName;

  @Autowired
  private MessageService messageService;

  @Autowired
  private Action<State, Event> errorAction;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    QuestionPayload payloadThirdAnswer = (QuestionPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String id = payloadThirdAnswer.getUser();
    User stateEntity = stateMachineRepository.findByUserID(id).get();
    stateEntity.setThirdAnswerAboutRules(payloadThirdAnswer.getAnswer());
    stateMachineRepository.save(stateEntity);
    messageService
        .sendBlocksMessage(messageService.getUserById(id), messageAboutSeveralInfoChannel);
    messageService.sendBlocksMessage(messageService.getUserById(id), addGitName);
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(THIRD_QUESTION)
        .target(AGREED_LICENSE)
        .event(CONSENT_TO_INFORMATION)
        .action(this, errorAction);
  }
}