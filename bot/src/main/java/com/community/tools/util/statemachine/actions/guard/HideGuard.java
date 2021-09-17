package com.community.tools.util.statemachine.actions.guard;

import com.community.tools.model.Messages;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;

import java.io.IOException;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.guard.Guard;

@WithStateMachine
public class HideGuard implements Guard<State, Event> {

  @Autowired
  private GitHubService gitHubService;

  @Autowired
  private MessageService messageService;

  @SneakyThrows
  @Override
  public boolean evaluate(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    String nickName = stateContext.getExtendedState().getVariables().get("gitNick").toString();
    messageService.sendPrivateMessage(messageService.getUserById(user),
        Messages.CHECK_NICK_NAME + nickName);
    boolean nicknameMatch = false;
    try {
      nicknameMatch = gitHubService.getUserByLoginInGitHub(nickName).getLogin().equals(nickName);
    } catch (IOException e) {
      messageService.sendPrivateMessage(messageService.getUserById(user),
          Messages.FAILED_NICK_NAME);
    }
    return nicknameMatch;
  }
}
