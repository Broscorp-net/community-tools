package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.PurchaseEvent;
import com.community.tools.util.statemachie.PurchaseState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

public class AgreeLicenseAction implements Action<PurchaseState, PurchaseEvent> {



  @Override
  public void execute(StateContext<PurchaseState, PurchaseEvent> stateContext) {
    // TODO: 22.04.2020 Добавить логику для пользоваельского соглашения
    System.out.println("Юзер принял пльзовательское соглашение");
  }
}
