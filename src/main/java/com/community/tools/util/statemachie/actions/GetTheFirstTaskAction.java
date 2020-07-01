package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class GetTheFirstTaskAction implements Action<State, Event> {

  @Value("${getFirstTask}")
  private String getFirstTask;
  @Autowired
  private SlackService slackService;

  @Override
  public void execute(final StateContext<State, Event> context) {
    String user = context.getExtendedState().getVariables().get("id").toString();
    try {
      slackService.sendBlocksMessage(user, getFirstTask);
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
