package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.StateMachineApplicationListeer;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.logging.Logger;


public class AddGitNameAction implements Action<State, Event> {

    private static Logger logger = Logger.getLogger(AddGitNameAction.class.getName());

    @Override
    public void execute(final StateContext<State, Event> context) {
        // TODO: 22.04.2020 add logic to add git name
        logger.info("Added git nick name");
    }
}
