package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class AddGitNameAction implements Action<State, Event> {

  @Override
  public void execute(final StateContext<State, Event> context) {
    // TODO: 22.04.2020 add logic to add git name
    System.out.println("Added git nick name");
  }
}
