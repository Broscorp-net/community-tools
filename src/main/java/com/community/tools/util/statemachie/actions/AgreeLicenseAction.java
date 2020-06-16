package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.StateMachineApplicationListeer;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.logging.Logger;

public class AgreeLicenseAction implements Action<State, Event> {

  private static Logger logger = Logger.getLogger(AgreeLicenseAction.class.getName());

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    // TODO: 22.04.2020 add logic for agree license
    logger.info("user agreed with licence");
  }
}
