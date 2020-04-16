package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.PurchaseEvent.BUY;
import static com.community.tools.util.statemachie.PurchaseEvent.RESERVE;
import static com.community.tools.util.statemachie.PurchaseEvent.RESERVE_DECLINE;
import static com.community.tools.util.statemachie.PurchaseState.CANCEL_RESERVED;
import static com.community.tools.util.statemachie.PurchaseState.NEW;
import static com.community.tools.util.statemachie.PurchaseState.PURCHASE_COMPLETE;
import static com.community.tools.util.statemachie.PurchaseState.RESERVED;

import com.community.tools.service.github.GitHubHookServlet;
import com.community.tools.util.statemachie.actions.BuyAction;
import com.community.tools.util.statemachie.actions.CancelAction;
import com.community.tools.util.statemachie.actions.ErrorAction;
import com.community.tools.util.statemachie.actions.HideGuard;
import com.community.tools.util.statemachie.actions.ReservedAction;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
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


  @Override
  public void configure(final StateMachineStateConfigurer<PurchaseState, PurchaseEvent> states)
      throws Exception {
    states.withStates().initial(NEW).end(PURCHASE_COMPLETE).states(EnumSet.allOf(PurchaseState.class));
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
        .source(NEW)
        .target(RESERVED)
        .event(RESERVE)
        .action(reservedAction(), errorAction())

        .and()
        .withExternal()
        .source(RESERVED)
        .target(CANCEL_RESERVED)
        .event(RESERVE_DECLINE)
        .action(cancelAction(), errorAction())

        .and()
        .withExternal()
        .source(RESERVED)
        .target(PURCHASE_COMPLETE)
        .event(BUY)
        .guard(hideGuard())
        .action(buyAction(), errorAction());

  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> reservedAction() {
    return new ReservedAction();
  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> cancelAction() {
    return new CancelAction();
  }

  @Bean
  public Action<PurchaseState, PurchaseEvent> buyAction() {
    return new BuyAction();
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
    return new DefaultStateMachinePersister<>(new PurchaseStateMachinePersister());
  }
}