package com.community.tools.slack;

import com.community.tools.model.Message;
import com.community.tools.service.EventListener;

import com.github.seratch.jslack.api.model.event.MessageEvent;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.MessageHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessagePayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Profile("slack")
public class SlackHandlerService {

  @Value("${idOfSlackBot}")
  private String idOfSlackBot;
  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;

  @Autowired
  private EventListener listener;

  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {
      String userId = teamJoinPayload.getEvent().getUser().getId();
    }
  };

  private MessageHandler messageHandler = new MessageHandler() {
    @Override
    public void handle(MessagePayload teamJoinPayload) {
      MessageEvent messageEvent = teamJoinPayload.getEvent();
      if (!messageEvent.getUser().equals(idOfSlackBot)) {
        String messageFromUser = messageEvent.getText();
        String userId = messageEvent.getUser();

        Message message = new Message(userId, messageFromUser);
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
      if (!testModeSwitcher) {
        dispatcher.register(teamJoinHandler);
      }
      dispatcher.register(messageHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}
