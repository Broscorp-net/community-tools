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
      tasks.add(0,"a_patform");
      tasks.add(1,"b_bytecode");
      tasks.add(2,"d_gc");
      tasks.add(3,"e_primitives");
      tasks.add(4,"f_boxing");
      tasks.add(5,"gvalueref");
      tasks.add(6,"hgenerics");
      tasks.add(7,"i_equals_hashcode");
      tasks.add(8,"j_exceptions");
      tasks.add(9,"k_classpath");
      tasks.add(10,"l_inner_classes");
      tasks.add(11,"m_override_overload");
      tasks.add(12,"n_strings");
      tasks.add(13,"o_git");

      int i = (Integer)context.getExtendedState().getVariables().get("taskNumber");
      String getFirstTask =
          "[{\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"Here is your next <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/"
              + tasks.get(i) + "|TASK>.\"}}]";
      String user = context.getExtendedState().getVariables().get("id").toString();
     try {
        slackService.sendBlocksMessage(slackService.getUserById(user), getFirstTask);
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  }