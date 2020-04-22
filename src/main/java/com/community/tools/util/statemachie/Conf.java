package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.PurchaseEvent.ADD_GIT_NAME;
import static com.community.tools.util.statemachie.PurchaseEvent.AGREE_LICENSE;
import static com.community.tools.util.statemachie.PurchaseEvent.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.PurchaseState.ADDED_GIT;
import static com.community.tools.util.statemachie.PurchaseState.NEW_USER;
import static com.community.tools.util.statemachie.PurchaseState.GOT_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.PurchaseState.AGREED_LICENSE;

import com.community.tools.util.statemachie.actions.GetTheFirstTaskAction;
import com.community.tools.util.statemachie.actions.AddGitNameAction;
import com.community.tools.util.statemachie.actions.ErrorAction;
import com.community.tools.util.statemachie.actions.HideGuard;
import com.community.tools.util.statemachie.actions.AgreeLicenseAction;
import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

@Configuration
@EnableStateMachineFactory
public class Conf extends EnumStateMachineConfigurerAdapter<PurchaseState, PurchaseEvent> {

  @Autowired
  PurchaseStateMachinePersister persister;

  @Override
  public void configure(final StateMachineStateConfigurer<PurchaseState, PurchaseEvent> states)
      throws Exception {
    states.withStates().initial(NEW_USER).end(GOT_THE_FIRST_TASK).states(EnumSet.allOf(PurchaseState.class));
  }

  @Override
  public void configure(
      final StateMachineConfigurationConfigurer<PurchaseState, PurchaseEvent> config)
      throws Exception {
    config
        .withConfiguration()
        .autoStartup(true)
        .listener(new PurchaseStateMachineApplicationListeer());
  }

  @Override
  public  void configure(final StateMachineTransitionConfigurer<PurchaseState,PurchaseEvent> transitions)
    throws Exception{
    transitions
        .withExternal()
        .source(NEW_USER)
        .target(AGREED_LICENSE)
        .event(AGREE_LICENSE)
        .action(agreeLicenseAction(), errorAction())

        .and()
        .withExternal()
        .source(AGREED_LICENSE)
        .target(ADDED_GIT)
        .event(ADD_GIT_NAME)
        .action(addGitNameAction(), errorAction())

        .and()
        .withExternal()
        .source(ADDED_GIT)
        .target(GOT_THE_FIRST_TASK)
        .event(GET_THE_FIRST_TASK)
        .action(getTheFirstTaskAction(), errorAction());

  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> agreeLicenseAction() {
    return new AgreeLicenseAction();
  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> addGitNameAction() {
    return new AddGitNameAction();
  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> getTheFirstTaskAction() {
    return new GetTheFirstTaskAction();
  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> errorAction() {
    return new ErrorAction();
  }

  @Bean
  public Guard<PurchaseState, PurchaseEvent> hideGuard() {
    return new HideGuard();
  }

  @Bean
  public StateMachinePersister<PurchaseState, PurchaseEvent, String> persister() {
    return new DefaultStateMachinePersister<>(persister);
  }
}