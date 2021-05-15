package com.community.tools.util.statemachine.actions.transitions.information;

import com.community.tools.model.User;
import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.service.discord.MessagesToDiscord;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.slack.MessagesToSlack;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import java.util.Map;
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
  private Action<State, Event> errorAction;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Autowired
  private BlockService blockService;

  @Autowired
  private Map<String, MessageService> messageServiceMap;

  @Value("${currentMessageService}")
  private String currentMessageService;

  /**
   * Selected current message service.
   * @return current message service
   */
  public MessageService getMessageService() {
    return messageServiceMap.get(currentMessageService);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    QuestionPayload payloadThirdAnswer = (QuestionPayload) stateContext.getExtendedState()
        .getVariables().get("dataPayload");
    String id = payloadThirdAnswer.getUser();
    User stateEntity = stateMachineRepository.findByUserID(id).get();
    stateEntity.setThirdAnswerAboutRules(payloadThirdAnswer.getAnswer());
    stateMachineRepository.save(stateEntity);
    getMessageService()
        .sendBlocksMessage(getMessageService().getUserById(id), blockService.createBlockMessage(
            MessagesToSlack.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL,
            MessagesToDiscord.MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL));
    getMessageService().sendBlocksMessage(getMessageService().getUserById(id),
        blockService.createBlockMessage(MessagesToSlack.ADD_GIT_NAME,
            MessagesToDiscord.ADD_GIT_NAME));
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