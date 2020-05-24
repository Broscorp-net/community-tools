package com.community.tools.controller;

import static com.community.tools.util.statemachie.Event.*;
import static com.community.tools.util.statemachie.State.*;
import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.model.User.Profile;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload.Action;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("app")
public class GitSlackUsersController {

  @Autowired
  private StateMachineFactory<State, Event> factory;
  @Autowired
  private StateMachinePersister<State, Event, String> persister;

  private final SlackService usersService;
  private final GitHubService gitService;

  @GetMapping(value = "/git", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getGitHubAllUsers() {
    Set<GHUser> gitHubAllUsers = gitService.getGitHubAllUsers();

    List<String> listGitUsersLogin = gitHubAllUsers.stream().map(GHPerson::getLogin)
        .collect(Collectors.toList());

    return ok().body(listGitUsersLogin);
  }

  @GetMapping(value = "/slack", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getSlackAllUsers() {
    Set<User> allSlackUsers = usersService.getAllUsers();

    List<String> listSlackUsersName = allSlackUsers.stream()
        .map(User::getProfile)
        .map(Profile::getDisplayName).collect(Collectors.toList());

    return ok().body(listSlackUsersName);
  }


  @GetMapping(value = "/sendTestMessage", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getAllEvents() throws ParseException {
    String message = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"image\",\n"
        + "\t\t\"title\": {\n"
        + "\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\"text\": \"image1\",\n"
        + "\t\t\t\"emoji\": true\n"
        + "\t\t},\n"
        + "\t\t\"image_url\": \"https://api.slack.com/img/blocks/bkb_template_images/beagle.png\",\n"
        + "\t\t\"alt_text\": \"image1\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"Read and confirm that you agree to our <https://www.youtube.com/watch?v=O6YzU00oack|rules> BOY :v:. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Agree\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"1_Agree\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    try {
      usersService.sendEventsMessage("roman", message);
    } catch (IOException | SlackApiException e) {
      e.printStackTrace();
    }
  }

  @GetMapping(value = "/sendTestMessage2", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getSendMessage() throws ParseException {
    String message = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"This is not work button. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Button\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"1_bad\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    try {
      usersService.sendEventsMessage("roman", message);
    } catch (IOException | SlackApiException e) {
      e.printStackTrace();
    }
  }

  @GetMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getSendMessageNewUser() throws ParseException {
    String message = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"New User. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Add\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"AddUser\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    try {
      usersService.sendEventsMessage("roman", message);
    } catch (IOException | SlackApiException e) {
      e.printStackTrace();
    }
  }

  @GetMapping(value = "/addUserToStateMachine", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getUserToStateMachine() throws Exception {

    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    persister.persist(machine, "rr.zagorulko");
  }

  @RequestMapping(value = "/slack/action", method = RequestMethod.POST)
  public void action(@RequestParam(name = "payload") String payload) throws Exception {

    String agreeMessage = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"First of all, you need to agree with rules. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Agree\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"AGREE_LICENSE\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";

    String addGitName = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"Now you should add your git name. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"AddedGitName\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"ADD_GIT_NAME\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";

    String getFirstTask = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"ICE, now pick first task. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"GetTask\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"GET_THE_FIRST_TASK\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    String theEnd = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"This is the end for now, the button will do nothing. \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"DO NOTHING\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"theEnd\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    String noOneCase = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"divider\"\n"
        + "\t},\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"NO ONE CASE \"\n"
        + "\t\t},\n"
        + "\t\t\"accessory\": {\n"
        + "\t\t\t\"type\": \"button\",\n"
        + "\t\t\t\"text\": {\n"
        + "\t\t\t\t\"type\": \"plain_text\",\n"
        + "\t\t\t\t\"text\": \"Button\",\n"
        + "\t\t\t\t\"emoji\": true\n"
        + "\t\t\t},\n"
        + "\t\t\t\"value\": \"Button\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";
    String notThatMessage = "[\n"
        + "\t{\n"
        + "\t\t\"type\": \"section\",\n"
        + "\t\t\"text\": {\n"
        + "\t\t\t\"type\": \"mrkdwn\",\n"
        + "\t\t\t\"text\": \"Please answer the latest message :ghost:\"\n"
        + "\t\t}\n"
        + "\t}\n"
        + "]";

    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);
/*    String message = "Message : \n\n  Name:\n " + pl.getUser().getName() + "\n\n"
        + "User:\n " + pl.getUser() + "\n\n"
        + " Type:\n " + pl.getType() + "\n\n"
        + " AppID:\n " + pl.getApiAppId() + "\n\n"
        + " ResponseUrl:\n " + pl.getResponseUrl() + "\n\n"
        + " Token:\n " + pl.getToken() + "\n\n"
        + " TriggerId:\n " + pl.getTriggerId() + "\n\n"
        + " Actions:\n " + pl.getActions() + "\n\n"
        + " Channel:\n " + pl.getChannel() + "\n\n"
        + " Container: \n" + pl.getContainer() + "\n\n"
        + " Team:\n " + pl.getTeam() + "\n\n"
        + " Message:\n " + pl.getMessage() + "\n\n";*/

    StateMachine<State, Event> machine = factory.getStateMachine();

    switch (pl.getActions().get(0).getValue()) {
      case "AddUser":
        persister.persist(machine, pl.getUser().getName());
        usersService.sendEventsMessage("roman", agreeMessage);
        usersService.sendPrivateMessage("roman", "Machine: " + machine.getState().getId());
        break;
      case "AGREE_LICENSE":
        persister.restore(machine, pl.getUser().getName());
        if(machine.getState().getId() == NEW_USER ) {
          machine.sendEvent(AGREE_LICENSE);
          persister.persist(machine, pl.getUser().getName());
          usersService.sendEventsMessage("roman", addGitName);
        }else{
          usersService.sendEventsMessage("roman", notThatMessage);
        }
        usersService.sendPrivateMessage("roman", "Machine: " + machine.getState().getId());
        break;
      case "ADD_GIT_NAME":
        persister.restore(machine, pl.getUser().getName());
        if(machine.getState().getId() == AGREED_LICENSE ) {
          machine.sendEvent(ADD_GIT_NAME);
          persister.persist(machine, pl.getUser().getName());
          usersService.sendEventsMessage("roman", getFirstTask);
        }else {
          usersService.sendEventsMessage("roman", notThatMessage);
        }
        usersService.sendPrivateMessage("roman", "Machine: " + machine.getState().getId());
        break;
      case "GET_THE_FIRST_TASK":
        persister.restore(machine, pl.getUser().getName());
        if(machine.getState().getId() == ADDED_GIT ) {
        machine.sendEvent(GET_THE_FIRST_TASK);
        persister.persist(machine, pl.getUser().getName());
        usersService.sendEventsMessage("roman", theEnd);
    }else {
      usersService.sendEventsMessage("roman", notThatMessage);
    }
        usersService.sendPrivateMessage("roman", "Machine: " + machine.getState().getId());
        break;
      case "theEnd":
        if(machine.getState().getId() == GOT_THE_FIRST_TASK ) {
        usersService
            .sendPrivateMessage("roman", "that was the end, congrats, stop pushing the button");
        }else {
          usersService.sendEventsMessage("roman", notThatMessage);
        }
        break;
      default:
        usersService.sendEventsMessage("roman", noOneCase);
        usersService.sendPrivateMessage("roman", "Machine: " + machine.getState().getId());

    }
  }
}
