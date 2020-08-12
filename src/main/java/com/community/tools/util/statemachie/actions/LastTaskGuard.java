package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class LastTaskGuard implements Guard<State, Event>  {

  @Value("${git.number.of.tasks}")
  private Integer numberOfTasks;

  @Override
  public boolean evaluate(StateContext<State, Event> stateContext) {
        return stateContext.getExtendedState().getVariables().get("taskNumber").equals(numberOfTasks);
  }
}
