package com.community.tools.util.statemachie;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceReal implements PurchaseService {

  private final StateMachinePersister<PurchaseState, PurchaseEvent, String> persister;
  private final StateMachineFactory<PurchaseState, PurchaseEvent> stateMachineFactory;

  public PurchaseServiceReal(
      StateMachinePersister<PurchaseState, PurchaseEvent, String> persister,
      StateMachineFactory<PurchaseState, PurchaseEvent> stateMachineFactory) {
    this.persister = persister;
    this.stateMachineFactory = stateMachineFactory;
  }

  @Override
  public boolean reserved(String userId, String productId) {
    final StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory
        .getStateMachine();
    stateMachine.getExtendedState().getVariables().put("PRODUCT_ID", productId);
    stateMachine.sendEvent(PurchaseEvent.RESERVE);
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
    final StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
    try{
      persister.restore(stateMachine, userId);
      stateMachine.sendEvent(PurchaseEvent.RESERVE_DECLINE);
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean buy(String userId) {
    final StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
    try {
      persister.restore(stateMachine, userId);
      stateMachine.sendEvent(PurchaseEvent.BUY);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
