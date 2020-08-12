package com.community.tools.util.statemachie.actions;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;


public class HideGuard implements Guard<State, Event> {

  @Value("${checkNickName}")
  private String checkNickName;
  @Value("${failedCheckNickName}")
  private String failedCheckNickName;
  @Value("${addGitName}")
  private String addGitName;
  @Autowired
  private SlackService slackService;
  @Autowired
  private GitHubService gitHubService;

  @Override
  public boolean evaluate(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    String nickName = stateContext.getExtendedState().getVariables().get("gitNick").toString();
    try {
      slackService.sendPrivateMessage(slackService.getUserById(user),
          checkNickName + nickName);
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
    boolean nicknameMatch = gitHubService.getGitHubAllUsers().stream()
        .anyMatch(e -> e.getLogin().equals(nickName));
    if (!nicknameMatch) {
      try {
        slackService.sendPrivateMessage(slackService.getUserById(user), failedCheckNickName);
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
    return nicknameMatch;
  }
}
