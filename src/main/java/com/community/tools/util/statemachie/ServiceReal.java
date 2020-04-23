package com.community.tools.util.statemachie;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
public class ServiceReal implements StateService {

  private final StateMachinePersister<State, Event, String> persister;
  private final StateMachineFactory<State, Event> stateMachineFactory;

  public ServiceReal(
      StateMachinePersister<State, Event, String> persister,
      StateMachineFactory<State, Event> stateMachineFactory) {
    this.persister = persister;
    this.stateMachineFactory = stateMachineFactory;
  }

  @Override
  public boolean reserved(String userId, String productId) {
    final StateMachine<State, Event> stateMachine = stateMachineFactory
        .getStateMachine();
    stateMachine.getExtendedState().getVariables().put("PRODUCT_ID", productId);
    stateMachine.sendEvent(Event.AGREE_LICENSE);
    try {
      persister.persist(stateMachine,userId);
    }catch (final Exception e){
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean cancelReserve(String userId) {
    final StateMachine<State, Event> stateMachine = stateMachineFactory.getStateMachine();
    try{
      persister.restore(stateMachine, userId);
      stateMachine.sendEvent(Event.GET_THE_FIRST_TASK);
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean buy(String userId) {
    final StateMachine<State, Event> stateMachine = stateMachineFactory.getStateMachine();
    try {
      persister.restore(stateMachine, userId);
      stateMachine.sendEvent(Event.ADD_GIT_NAME);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
