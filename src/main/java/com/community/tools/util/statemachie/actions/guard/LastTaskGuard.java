package com.community.tools.util.statemachie.actions.guard;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.guard.Guard;

@WithStateMachine
public class LastTaskGuard implements Guard<State, Event> {

  @Value("${git.number.of.tasks}")
  private Integer numberOfTasks;

  @Override
  public boolean evaluate(StateContext<State, Event> stateContext) {
    return stateContext.getExtendedState().getVariables()
            .get("taskNumber").equals(numberOfTasks - 1);
  }
}
