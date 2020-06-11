package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class AgreeLicenseAction implements Action<State, Event> {



  @Override
  public void execute(StateContext<State, Event> stateContext) {
    // TODO: 22.04.2020 add logic for agree license
    System.out.println("user agreed with licence");
  }
}
