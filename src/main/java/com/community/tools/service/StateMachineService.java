package com.community.tools.service;

import static com.community.tools.util.statemachie.Event.*;
import static com.community.tools.util.statemachie.State.AGREED_LICENSE;
import static com.community.tools.util.statemachie.State.GOT_THE_FIRST_TASK;
import static com.community.tools.util.statemachie.State.NEW_USER;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StateMachineService {

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Value("${welcome}")
  private String welcome;
  @Value("${checkNickName}")
  private String checkNickName;


  @Value("${failedCheckNickName}")
  private String failedCheckNickName;
  @Value("${doNotUnderstandWhatTodo}")
  private String doNotUnderstandWhatTodo;

  @Value("${noOneCase}")
  private String noOneCase;
  @Value("${notThatMessage}")
  private String notThatMessage;
  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;

  private final GitHubService gitHubService;
  private final SlackService slackService;

  public void agreeForGitHubNickName(String nickName, String userId) throws Exception {
    String user = slackService.getUserById(userId);

    StateMachine<State, Event> machine = restoreMachine(userId);

    if (machine.getState().getId() == AGREED_LICENSE) {
      slackService.sendPrivateMessage(user,
          checkNickName + nickName);

      boolean nicknameMatch = gitHubService.getGitHubAllUsers().stream()
          .anyMatch(e -> e.getLogin().equals(nickName));
      if (nicknameMatch) {

        machine.sendEvent(ADD_GIT_NAME);
        machine.sendEvent(GET_THE_FIRST_TASK);
        persistMachine(machine, userId);

        StateEntity stateEntity = stateMachineRepository.findByUserID(userId).get();
        stateEntity.setGitName(nickName);
        stateMachineRepository.save(stateEntity);

      } else {
        slackService.sendPrivateMessage(user, failedCheckNickName);
      }

    } else {
      slackService.sendPrivateMessage(user, doNotUnderstandWhatTodo);

    }
  }

  public void checkActionsFromButton(String action, String userId) throws Exception {
    StateMachine<State, Event> machine = restoreMachine(userId);
    String user = slackService.getUserById(userId);
    switch (action) {
      case "AGREE_LICENSE":
        if (machine.getState().getId() == NEW_USER) {
          machine.sendEvent(FIRST_AGREE_MESS);
          persistMachine(machine, userId);
        } else {
          slackService.sendBlocksMessage(user, notThatMessage);
        }
        break;
      case "theEnd":

        if (machine.getState().getId() == GOT_THE_FIRST_TASK) {
          machine.sendEvent(GET_THE_FIRST_TASK);
          slackService
              .sendPrivateMessage(user, "that was the end, congrats");
        } else {
          slackService.sendBlocksMessage(user, notThatMessage);
        }
        break;
      default:
        slackService.sendBlocksMessage(user, noOneCase);

    }
  }

  public StateMachine<State, Event> restoreMachine(String id) throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    persister.restore(machine, id);
    return machine;
  }
  public StateMachine<State, Event> restoreMachineByNick(String nick) {
    StateEntity stateEntity = stateMachineRepository.findByGitName(nick).get();
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    try {
      persister.restore(machine, stateEntity.getUserID());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return machine;
  }
  public String getIdByNick(String nick){
    return stateMachineRepository.findByGitName(nick).get().getUserID();
  }

  public void persistMachine(StateMachine<State, Event> machine, String id){
    try {
      persister.persist(machine, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void persistMachineForNewUser(String id) throws Exception {
    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.getExtendedState().getVariables().put("id", id);
    machine.getExtendedState().getVariables().put("taskNumber", 1);
    machine.getExtendedState().getVariables().put("mentor", "NO_MENTOR");
    machine.start();
    persister.persist(machine, id);
  }
}
