package com.community.tools.service.slack;

import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;

import com.github.seratch.jslack.app_backend.events.handler.AppMentionHandler;
import com.github.seratch.jslack.app_backend.events.payload.AppMentionPayload;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;

import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.handler.MessageBotHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessageBotPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        slackService.sendPrivateMessage(teamJoinPayload.getEvent().getUser().getRealName(),
            "Welcome to the club buddy :dealwithit:");
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  };
  private AppMentionHandler appMentionHandler = new AppMentionHandler() {
    @Override
    public void handle(AppMentionPayload teamJoinPayload) {
      if(teamJoinPayload.getEvent().getText().contains("@Brobot My git name is \""))
      {
        String message = teamJoinPayload.getEvent().getText().replaceAll("@Brobot My git name is ", "");
        message = message.replaceAll("\"","");
        try {
          slackService.sendPrivateMessage("roman",
              "ok i'll chesck your message "+ message);
        } catch (IOException | SlackApiException e) {
          throw new RuntimeException(e);
        }
      }else {
        try {
          slackService.sendPrivateMessage("roman",
              teamJoinPayload.getEvent().getText());
        } catch (IOException | SlackApiException e) {
          throw new RuntimeException(e);
        }
      }
    }
  };

  private MessageBotHandler messageBotHandler = new MessageBotHandler() {
    @Override
    public void handle(MessageBotPayload teamJoinPayload) {
      try {

        slackService.sendPrivateMessage("roman",
            teamJoinPayload.getEvent().getText());
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  };

  public class GreatNewMemberServlet extends SlackEventsApiServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
        slackService.sendPrivateMessage("roman",
            "maybe, just maybe, some one press the button");
      } catch (SlackApiException e) {
        e.printStackTrace();
      }
      super.doPost(req, resp);
    }
    @Override
    protected void setupDispatcher(EventsDispatcher dispatcher) {
      dispatcher.register(teamJoinHandler);
      dispatcher.register(appMentionHandler);

      dispatcher.register(messageBotHandler);

    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}
