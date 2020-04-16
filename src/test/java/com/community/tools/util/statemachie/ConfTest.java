package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.PurchaseEvent.BUY;
import static com.community.tools.util.statemachie.PurchaseEvent.RESERVE;
import static com.community.tools.util.statemachie.PurchaseEvent.RESERVE_DECLINE;
import static com.community.tools.util.statemachie.PurchaseState.CANCEL_RESERVED;
import static com.community.tools.util.statemachie.PurchaseState.NEW;
import static com.community.tools.util.statemachie.PurchaseState.PURCHASE_COMPLETE;
import static com.community.tools.util.statemachie.PurchaseState.RESERVED;

import com.community.tools.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfTest {

  @Autowired
  private StateMachineFactory<PurchaseState, PurchaseEvent> factory;
  @Autowired
  private PurchaseController purchaseController;
  @Autowired
  private ApplicationContext conf;

  @Test
  public void contextLoads() {
  }

  @Test
  public void testWhenReservedCancel() throws Exception {
    StateMachine<PurchaseState, PurchaseEvent> machine = factory.getStateMachine();
    StateMachineTestPlan<PurchaseState, PurchaseEvent> plan =
        StateMachineTestPlanBuilder.<PurchaseState, PurchaseEvent>builder()
            .defaultAwaitTime(2)
            .stateMachine(machine)
            .step()
            .expectStates(NEW)
            .expectStateChanged(0)
            .and()
            .step()
            .sendEvent(RESERVE)
            .expectState(RESERVED)
            .expectStateChanged(1)
            .and()
            .step()
            .sendEvent(RESERVE_DECLINE)
            .expectState(CANCEL_RESERVED)
            .expectStateChanged(1)
            .and()
            .build();
    plan.test();
  }

  @Test
  public void testWhenPurchaseComplete() throws Exception {

    StateMachine<PurchaseState, PurchaseEvent> machine = factory.getStateMachine();
    StateMachineTestPlan<PurchaseState, PurchaseEvent> plan =
        StateMachineTestPlanBuilder.<PurchaseState, PurchaseEvent>builder()
            .defaultAwaitTime(2)
            .stateMachine(machine)
            .step()
            .expectStates(NEW)
            .expectStateChanged(0)
            .and()
            .step()
            .sendEvent(RESERVE)
            .expectState(RESERVED)
            .expectStateChanged(1)
            .and()
            .step()
            .sendEvent(BUY)
            .expectState(PURCHASE_COMPLETE)
            .expectStateChanged(1)
            .and()
            .build();
    plan.test();
  }

//  @Test
//  public void newTest() throws Exception {
//    PurchaseStateMachinePersister stateMachinePersist = new PurchaseStateMachinePersister();
//    StateMachinePersister<PurchaseState, PurchaseEvent, String> persister = new DefaultStateMachinePersister<>(
//        stateMachinePersist);
//    StateMachine<String, String> stateMachine1 = conf.getBean("machine1", StateMachine.class);
//    StateMachine<String, String> stateMachine2 = conf.getBean("machine2", StateMachine.class);
//    stateMachine1.start();
//    stateMachine1.sendEvent("RESERVE");
//  }
}