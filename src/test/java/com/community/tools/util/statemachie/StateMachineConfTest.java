package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.Event.ADD_GIT_NAME;
import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
import static com.community.tools.util.statemachie.Event.GET_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.ADDED_GIT;
import static com.community.tools.util.statemachie.State.NEW_USER;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StateMachineConfTest {

  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;


  @Autowired
  private ApplicationContext conf;

  @Test
  public void contextLoads() {
  }

  @Test
  public void testPersist() throws Exception {

    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    machine.sendEvent(AGREE_LICENSE);
    persister.persist(machine, "007");

    StateMachine<State, Event> machine2 = factory.getStateMachine();
    persister.restore(machine2, "007");

    assertEquals(machine.getId(), machine2.getId());
  }


  @Test
  public void testWhenAddGitName() throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    StateMachineTestPlan<State, Event> plan =
        StateMachineTestPlanBuilder.<State, Event>builder()
            .defaultAwaitTime(2)
            .stateMachine(machine)
            .step()
            .expectStates(NEW_USER)
            .expectStateChanged(0)
            .and()
            .step()
            .sendEvent(AGREE_LICENSE)
            .expectState(AGREED_LICENSE)
            .expectStateChanged(1)
            .and()
            .step()
            .sendEvent(ADD_GIT_NAME)
            .expectState(ADDED_GIT)
            .expectStateChanged(1)
            .and()
            .build();
    plan.test();
  }

  @Test
  public void testWhenPurchaseComplete() throws Exception {

    StateMachine<State, Event> machine = factory.getStateMachine();
    StateMachineTestPlan<State, Event> plan =
        StateMachineTestPlanBuilder.<State, Event>builder()
            .defaultAwaitTime(2)
            .stateMachine(machine)
            .step()
            .expectStates(NEW_USER)
            .expectStateChanged(0)
            .and()
            .step()
            .sendEvent(AGREE_LICENSE)
            .expectState(AGREED_LICENSE)
            .expectStateChanged(1)
            .and()
            .step()
            .sendEvent(ADD_GIT_NAME)
            .expectState(ADDED_GIT)
            .expectStateChanged(1)
            .and()
            .step()
            .sendEvent(GET_THE_FIRST_TASK)
            .expectState(GOT_THE_FIRST_TASK)
            .expectStateChanged(1)
            .and()
            .build();
    plan.test();
  }
}