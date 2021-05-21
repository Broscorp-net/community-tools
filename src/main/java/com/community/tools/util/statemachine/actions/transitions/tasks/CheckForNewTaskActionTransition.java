package com.community.tools.util.statemachine.actions.transitions.tasks;

import static com.community.tools.util.statemachine.Event.GET_THE_NEW_TASK;
import static com.community.tools.util.statemachine.State.CHECK_FOR_NEW_TASK;
import static com.community.tools.util.statemachine.State.GOT_THE_TASK;

import com.community.tools.model.Messages;
import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class CheckForNewTaskActionTransition implements Transition {

  @Autowired
  private MessageService messageService;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  @Autowired
  private Action<State, Event> errorAction;

  @Autowired
  private BlockService blockService;

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(GOT_THE_TASK)
        .target(CHECK_FOR_NEW_TASK)
        .event(GET_THE_NEW_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    List<String> tasksList = Arrays.asList(tasksForUsers);

    int i = (Integer) stateContext.getExtendedState().getVariables().get("taskNumber");
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    messageService.sendBlocksMessage(messageService.getUserById(user),
        blockService.nextTaskMessage(tasksList, i));
  }
}
