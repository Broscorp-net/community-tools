package com.community.tools.util.statemachie.actions;

import com.community.tools.util.statemachie.PurchaseEvent;
import com.community.tools.util.statemachie.PurchaseState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;


public class GetTheFirstTaskAction implements Action<PurchaseState, PurchaseEvent> {

  @Override
  public void execute(final StateContext<PurchaseState, PurchaseEvent> context) {
    // TODO: 22.04.2020 Добавить логику для получения юзером первого задания
    System.out.println("Юзер получил первое задание");
  }
}
