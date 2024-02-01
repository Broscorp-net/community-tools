package com.community.tools.util.statemachine.actions.transitions.verifications;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.ClassroomService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.actions.Transition;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@WithStateMachine
@Slf4j
@RequiredArgsConstructor
public class AddGitNameActionTransition implements Transition {

  private final Action<State, Event> errorAction;
  private final UserRepository userRepository;
  private final ClassroomService classroomService;
  private final MessageService<?> messageService;

  @Value("${newbieRole:newbie}")
  private String newbieRoleName;

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(State.CHECK_LOGIN)
        .target(State.GETTING_PULL_REQUEST)
        .event(Event.ADD_GIT_NAME_AND_FIRST_TASK)
        .action(this, errorAction);
  }

  @SneakyThrows
  @Override
  public void execute(StateContext<State, Event> stateContext) {
    VerificationPayload payload =
        (VerificationPayload) stateContext.getExtendedState().getVariables().get("dataPayload");
    String userId = payload.getId();
    String nickname = payload.getGitNick();

    User stateEntity = userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("User with id = [" + userId + "] was not found"));
    stateEntity.setGitName(nickname);
    GHUser userGitLogin = new GHUser();
    try {
      classroomService.addUserToTraineesTeam(nickname);
      stateEntity.setEmail(userGitLogin.getEmail());
    } catch (Exception e) {
      log.error("Failed to add a user with nickname = [{}] to a team", nickname);
    }
    userRepository.save(stateEntity);
    messageService.sendPrivateMessage(messageService.getUserById(userId),
        Messages.REGISTRATION_COMPLETED);
    stateContext.getExtendedState().getVariables().put("gitNick", nickname);
    messageService.removeRole(stateEntity.getGuildId(), userId, newbieRoleName);
  }

}
