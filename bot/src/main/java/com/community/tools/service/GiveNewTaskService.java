package com.community.tools.service;

import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
public class GiveNewTaskService {

  @Value("${git.number.of.tasks}")
  private Integer numberOfTasks;
  private final StateMachinePersister<State, Event, String> persister;

  public GiveNewTaskService(StateMachinePersister<State, Event, String> persister) {
    this.persister = persister;
  }

  /**
   * Give new Task to the trainee. Checks for the last task.
   *
   * @param userId - id user
   */
  public void giveNewTask(StateMachine<State, Event> machine, String userId, Integer taskNumber) {
    try {
      machine.sendEvent(Event.GET_THE_NEW_TASK);
      if (taskNumber.equals(numberOfTasks - 1)) {
        machine.sendEvent(Event.LAST_TASK);
      } else {
        machine.sendEvent(Event.CHANGE_TASK);
      }
      persister.persist(machine, userId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
