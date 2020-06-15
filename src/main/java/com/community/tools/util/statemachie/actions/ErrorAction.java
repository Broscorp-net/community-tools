package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.logging.Logger;


public class ErrorAction implements Action<State, Event> {
    private static Logger logger = Logger.getLogger(AgreeLicenseAction.class.getName());

    @Override
    public void execute(final StateContext<State, Event> context) {

    }
}
