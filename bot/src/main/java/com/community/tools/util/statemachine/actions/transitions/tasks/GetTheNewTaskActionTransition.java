package com.community.tools.util.statemachine.actions.transitions.tasks;

import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
public class GetTheNewTaskActionTransition implements Transition {
  private MessageService messageService;
  private MessageConstructor messageConstructor;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;
  private Action<State, Event> errorAction;

  public GetTheNewTaskActionTransition(@Lazy MessageService messageService,
                                       MessageConstructor messageConstructor,
                                       Action<State, Event> errorAction) {
    this.messageService = messageService;
    this.messageConstructor = messageConstructor;
    this.errorAction = errorAction;
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.GOT_THE_TASK)
        .target(State.CHECK_FOR_NEW_TASK)
        .event(Event.GET_THE_NEW_TASK)
        .action(this, errorAction);
  }

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    List<String> tasksList = Arrays.asList(tasksForUsers);

    int i = (Integer) stateContext.getExtendedState().getVariables().get("taskNumber");
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    messageService.sendBlocksMessage(messageService.getUserById(user),
        messageConstructor.createNextTaskMessage(tasksList, i));
  }
}
