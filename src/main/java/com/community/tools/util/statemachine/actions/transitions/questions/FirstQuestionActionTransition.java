package com.community.tools.util.statemachine.actions.transitions.questions;

import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.service.discord.MessagesToDiscord;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.slack.MessagesToSlack;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class FirstQuestionActionTransition implements Transition {

  @Value("${firstQuestion}")
  private String firstQuestion;

  @Autowired
  private BlockService blockService;

  @Autowired
  private Action<State, Event> errorAction;

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
    SinglePayload payload = (SinglePayload) stateContext.getExtendedState().getVariables()
        .get("dataPayload");
    String id = payload.getId();
    getMessageService().sendBlocksMessage(getMessageService().getUserById(id),
        blockService.createBlockMessage(
        MessagesToSlack.FIRST_QUESTION, MessagesToDiscord.FIRST_QUESTION));
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.NEW_USER)
        .target(State.FIRST_QUESTION)
        .event(Event.QUESTION_FIRST)
        .action(this, errorAction);
  }
}