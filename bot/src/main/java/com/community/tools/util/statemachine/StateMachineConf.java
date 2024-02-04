package com.community.tools.util.statemachine;

import com.community.tools.util.statemachine.actions.Transition;
import java.util.EnumSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;

@Configuration
@EnableStateMachineFactory
public class StateMachineConf extends EnumStateMachineConfigurerAdapter<State, Event> {

  @Autowired
  private StateMachinePersister persister;
  @Autowired
  List<Transition> transitionList;

  @Override
  public void configure(final StateMachineStateConfigurer<State, Event> states)
      throws Exception {
    states.withStates().initial(State.NEW_USER).states(EnumSet.allOf(State.class));
  }

  @Override
  public void configure(
      final StateMachineConfigurationConfigurer<State, Event> config)
      throws Exception {
    config
        .withConfiguration()
        .autoStartup(true)
        .listener(new StateMachineApplicationListener());
  }

  @Override
  public void configure(final StateMachineTransitionConfigurer<State, Event> transitions) {
    transitionList.forEach(transition -> {
      try {
        transition.configure(transitions);
      } catch (Exception e) {
        throw new StateMachineException("Transition configure exception");
      }
    });
  }

  @Bean
  public org.springframework.statemachine.persist.StateMachinePersister persister() {
    return new DefaultStateMachinePersister<>(persister);
  }
}