package com.community.tools.util.statemachie.actions.tasks;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ChangeTaskAction implements Action<State, Event> {

  @Override
  public void execute(final StateContext<State, Event> context) {
    int i = (Integer)context.getExtendedState().getVariables().get("taskNumber");
    context.getExtendedState().getVariables().put("taskNumber",++i);
  }
}
