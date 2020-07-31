package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class CheckForNewTaskAction implements Action<State, Event> {
    @Autowired
    private SlackService slackService;

    @Override
    public void execute(final StateContext<State, Event> context) {
      List<String> tasks = new ArrayList<>();
      tasks.add(0,"https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/a_patform");
      tasks.add(1,"https://www.youtube.com/watch?v=BIGsMPnqjkY&t");
      tasks.add(2,"https://coub.com/view/27e36d");
      tasks.add(3,"https://coub.com/view/2h06my");
      int i = (Integer)context.getExtendedState().getVariables().get("taskNumber");
      String getFirstTask =
          "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Here is your next <"
              + tasks.get(i) + "|TASK>. gl :face_with_cowboy_hat:\"}}]";
      String user = context.getExtendedState().getVariables().get("id").toString();
     try {
        slackService.sendBlocksMessage(slackService.getUserById(user), getFirstTask);
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  }