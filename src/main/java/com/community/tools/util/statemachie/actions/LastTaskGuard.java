package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class LastTaskGuard implements Guard<State, Event>  {

  @Override
  public boolean evaluate(StateContext<State, Event> stateContext) {
    return true;
  }
}
