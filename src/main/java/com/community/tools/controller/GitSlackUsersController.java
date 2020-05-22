package com.community.tools.controller;

import static com.community.tools.util.statemachie.Event.AGREE_LICENSE;
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

  @GetMapping(value = "/addUserToStateMachine", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getUserToStateMachine() throws Exception {

    StateMachine<State, Event> machine = factory.getStateMachine();
    machine.start();
    persister.persist(machine, "rr.zagorulko");
  }

  @RequestMapping(value = "/slack/action", method = RequestMethod.POST)
  public void action(@RequestParam(name = "payload") String payload) throws Exception {

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
    StringBuilder message2 = new StringBuilder("User:\n " + pl.getUser() + "\n\n" +
        " Message:\n " + pl.getMessage().toString() + "\n\n" +
        " Action:\n " + pl.getActions().toString() + "\n\n");
    try {
      usersService.sendPrivateMessage("roman", "Change the stateMachine: \n" + message2.toString());
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
    boolean changeMachine = false;
    for (Action action : pl.getActions()) {
      if (action.getValue().equals("1_Agree")) {
        StateMachine<State, Event> machine = factory.getStateMachine();
        StateMachine<State, Event> machine1 = factory.getStateMachine();
        persister.restore(machine, pl.getUser().toString());
        machine.sendEvent(AGREE_LICENSE);
        persister.persist(machine, pl.getUser().toString());
        persister.restore(machine1, pl.getUser().toString());
        message2.append("\n\nState of Machine : ").append(machine1.getState().getId());
        changeMachine = true;
      }
    }

    message2.append("\n\nChange Machine :").append(changeMachine);
    try {
      usersService.sendPrivateMessage("roman", "Change the stateMachine: \n" + message2.toString());
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }

  }
}
