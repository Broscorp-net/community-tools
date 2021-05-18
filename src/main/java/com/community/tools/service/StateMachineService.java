package com.community.tools.service;

import com.community.tools.model.User;
import com.community.tools.service.payload.Payload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StateMachineService {

  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;
  @Autowired
  GiveNewTaskService giveNewTaskService;
  @Autowired
  EstimateTaskService estimateTaskService;
  @Autowired
  MessageService messageService;

  /**
   * Restore machine by Slack`s userId.
   *
   * @param id Slack`s userId
   * @return StateMachine
   * @throws Exception Exception
   */
  public StateMachine<State, Event> restoreMachine(String id) throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    persister.restore(machine, id);
    return machine;
  }

  /**
   * Restore machine by GitHub Login.
   *
   * @param nick GitHub login
   * @return StateMachine
   */
  public StateMachine<State, Event> restoreMachineByNick(String nick) {
    User user = stateMachineRepository.findByGitName(nick).get();
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    try {
      persister.restore(machine, user.getUserID());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return machine;
  }

  public String getIdByNick(String nick) {
    return stateMachineRepository.findByGitName(nick).get().getUserID();
  }

  public String getUserByNick(String nick) {
    return messageService.getUserById(getIdByNick(nick));
  }

  /**
   * Persist machine for User by userId.
   *
   * @param machine StateMachine
   * @param id      Slack`s userId
   */
  public void persistMachine(StateMachine<State, Event> machine, String id) {
    try {
      persister.persist(machine, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Persist machine for new User by userId.
   *
   * @param id Slack`s userId
   * @throws Exception Exception
   */
  public void persistMachineForNewUser(String id) throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.getExtendedState().getVariables().put("id", id);
    machine.getExtendedState().getVariables().put("taskNumber", 1);
    machine.getExtendedState().getVariables().put("mentor", "NO_MENTOR");
    machine.start();
    persister.persist(machine, id);
  }

  /**
   * Method to start the action.
   *
   * @param payload - payload that stores data to execute Actions
   * @param event   - event for StateMachine
   */
  public void doAction(StateMachine<State, Event> machine, Payload payload, Event event) {
    machine.getExtendedState().getVariables().put("dataPayload", payload);
    machine.sendEvent(event);
    persistMachine(machine, payload.getId());
  }

  /**
   * Method to start the event by state.
   *
   * @param userId - id users
   * @param state  - state id
   * @param event  - event for state machine
   * @return - true if state id equals machine.stateId
   */
  public boolean doAction(String userId, State state, Event event) throws Exception {
    StateMachine<State, Event> machine = restoreMachine(userId);
    if (machine.getState().getId().equals(state)) {
      machine.sendEvent(event);
      persister.persist(machine, userId);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method for action 'radio_buttons-action'.
   *
   * @param value  - answer for button
   * @param userId - id users
   */
  public void estimate(String value, String userId)
      throws Exception {
    StateMachine<State, Event> machine = restoreMachine(userId);
    Integer taskNumber = (Integer) machine.getExtendedState().getVariables().get("taskNumber");

    estimateTaskService.saveEstimateTask(userId, taskNumber, value);
    giveNewTaskService.giveNewTask(userId);
  }
}