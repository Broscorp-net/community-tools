package com.community.tools.service.slack;

import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.handler.ChannelCreatedHandler;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;
import com.github.seratch.jslack.app_backend.events.payload.ChannelCreatedPayload;
import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GreetNewMemberService {

  private final SlackService slackService;
  private TeamJoinHandler teamJoinHandler = new TeamJoinHandler() {
    @Override
    public void handle(TeamJoinPayload teamJoinPayload) {
      try {
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getName(),
            "Welcome to the club buddy :dealwithit:");
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  };

  public class GreatNewMemberServlet extends SlackEventsApiServlet {

    @Override
    protected void setupDispatcher(EventsDispatcher dispatcher) {
      dispatcher.register(teamJoinHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }

  public ChannelCreatedHandler channelCreatedHandler = new ChannelCreatedHandler() {
    @Override
    public void handle(ChannelCreatedPayload teamJoinPayload) {

      try {
        slackService.sendPrivateMessage("roman",
            teamJoinPayload.getEvent().getChannel().getName() +" Welcome to the club buddy :dealwithit:");
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  };

  public class ChannelServlet extends SlackEventsApiServlet {

    @Override
    protected void setupDispatcher(EventsDispatcher dispatcher) {
      dispatcher.register(channelCreatedHandler);
    }
  }

  @Bean
  public ServletRegistrationBean<ChannelServlet> servletRegistrationBAMBean() {
    return new ServletRegistrationBean<>(new ChannelServlet(), "/channelCreated/*");
  }
}
