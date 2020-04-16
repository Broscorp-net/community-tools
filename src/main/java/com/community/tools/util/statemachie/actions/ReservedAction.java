package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.PurchaseEvent;
import com.community.tools.util.statemachie.PurchaseState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

public class ReservedAction implements Action<PurchaseState, PurchaseEvent> {



  @Override
  public void execute(StateContext<PurchaseState, PurchaseEvent> stateContext) {
    final  String productId = stateContext.getExtendedState().get("PRODUCT_ID", String.class);
    System.out.println("Товар с номером " + productId + " Зарезервирован");
  }
}
