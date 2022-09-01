package com.community.tools.service.github;

import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;

import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Deprecated
@Service
public class AddMentorService {
  @Value("${generalInformationChannel}")
  private String channel;
  @Autowired
  private StateMachineService stateMachineService;
  @Autowired
  private MentorsRepository mentorsRepository;

  @Autowired
  private MessageService messageService;

  /**
   * Add Mentor to the trainee, which make pull request.
   *
   * @param mentor  GitHub login of mentor
   * @param creator GitHub login of trainee
   */
  public void addMentor(String mentor, String creator) {
    if (mentorsRepository.findByGitNick(mentor).isPresent()) {
      StateMachine<State, Event> machine = stateMachineService.restoreMachineByNick(creator);
      machine.getExtendedState().getVariables().put("mentor", mentor);
      stateMachineService
              .persistMachine(machine, machine.getExtendedState()
                      .getVariables().get("id").toString());
    }
  }

  public boolean doesMentorExist(String user) {
    return !stateMachineService.restoreMachineByNick(user).getExtendedState().getVariables()
            .get("mentor").equals("NO_MENTOR");
  }

  /**
   * Send notify to channel with trainee GH login, url of pull and mentor GH login.
   *
   * @param user GitHub login of trainee
   * @param url  Url of pull request
   */
  public void sendNotifyWithMentor(String user, String url) throws IOException, SlackApiException {
    messageService
        .sendMessageToConversation(channel, "User " + user
         + " created a pull request \n url: " + url
         + "\n Please check it : <@" + mentorsRepository
           .findByGitNick(stateMachineService.restoreMachineByNick(user)
             .getExtendedState().getVariables().get("mentor").toString())
               .get().getSlackId() + ">");

  }

}

