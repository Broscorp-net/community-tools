package com.community.tools.service.slack;

import com.community.tools.service.StateMachineService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.MessageHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessagePayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class GreetNewMemberService {

  @Value("${welcome}")
  private String welcome;
  @Value("${idOfSlackBot}")
  private String idOfSlackBot;
  @Value("${agreeMessage}")
  private String agreeMessage;

  private final SlackService slackService;
  private final StateMachineService stateMachineService;
  @Autowired
  private StateMachineRepository stateMachineRepository;

  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {

      try {
        String user = teamJoinPayload.getEvent().getUser().getId();
        StateEntity stateEntity = new StateEntity();
        stateEntity.setUserID(user);
        stateMachineRepository.save(stateEntity);

        stateMachineService.persistMachineForNewUser(user);
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
            welcome);
        slackService
            .sendBlocksMessage(teamJoinPayload.getEvent().getUser().getRealName(), agreeMessage);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      if (!teamJoinPayload.getEvent().getUser().equals(idOfSlackBot)) {
        try {
          StateMachine<State, Event> machine = stateMachineService.restoreMachine(teamJoinPayload.getEvent().getUser());
          machine.getExtendedState().getVariables()
              .put("gitNick", teamJoinPayload.getEvent().getText());
          machine.sendEvent(Event.ADD_GIT_NAME);
          machine.sendEvent(Event.GET_THE_FIRST_TASK);
          stateMachineService.persistMachine(machine, teamJoinPayload.getEvent().getUser());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  };

  public class GreatNewMemberServlet extends SlackEventsApiServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      super.doPost(req, resp);
    }

    @Override
    protected void setupDispatcher(EventsDispatcher dispatcher) {
      dispatcher.register(teamJoinHandler);
      dispatcher.register(messageHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}
