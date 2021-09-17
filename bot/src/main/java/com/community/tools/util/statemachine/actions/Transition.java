package com.community.tools.util.statemachine.actions;

import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public interface Transition extends Action<State, Event> {
  void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception;
}
