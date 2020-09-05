package com.community.tools.service.github;

import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AddMentorService {

  @Autowired
  private StateMachineService stateMachineService;
  @Autowired
  private MentorsRepository mentorsRepository;
  @Autowired
  private SlackService service;

  public void addMentor(String mentor, String creator) {
    if (mentorsRepository.findByGitNick(mentor).isPresent()) {
      StateMachine<State, Event> machine = stateMachineService.restoreMachineByNick(creator);
      machine.getExtendedState().getVariables().put("mentor", mentor);
      stateMachineService
          .persistMachine(machine, machine.getExtendedState().getVariables().get("id").toString());
    }
  }

  public boolean doesMentorExist(String user) {
    return !stateMachineService.restoreMachineByNick(user).getExtendedState().getVariables()
        .get("mentor").equals("NO_MENTOR");
  }

  public void sendNotifyWithMentor(String user, String url) {
    try {
      service
          .sendMessageToChat("test_2", "User " + user + " create a pull request \n url: " + url
              + "\n Please check it : <@" + mentorsRepository
              .findByGitNick(stateMachineService.restoreMachineByNick(user)
                  .getExtendedState().getVariables().get("mentor").toString()).get().getSlackId()
              + ">");
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

}

