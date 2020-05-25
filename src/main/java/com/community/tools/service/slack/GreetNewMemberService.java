package com.community.tools.service.slack;

import com.community.tools.service.github.GitHubService;
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
  private final GitHubService gitHubService;
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
      if (teamJoinPayload.getEvent().getText().contains("My git name is ")) {
        String check = "My git name is ";
        String message = teamJoinPayload.getEvent().getText();
        int p = message.indexOf(check);
        message = message.substring(p + check.length());
        String nick = message;
       StringBuilder mes = new StringBuilder(message);
        gitHubService.getGitHubAllUsers().forEach(users-> {
          mes.append(users.getLogin()).append(" ");
          try {
            if(users.getLogin().equals(nick)){
              slackService.sendPrivateMessage("roman",
                  "congrats your nick available ");
            }else{
              slackService.sendPrivateMessage("roman",
                  "your nick is not available ");
            }
          } catch (IOException | SlackApiException e) {
            e.printStackTrace();
          }
        });

        try {
          slackService.sendPrivateMessage("roman",
              "ok i'll check your nick " + mes);
        } catch (IOException | SlackApiException e) {
          throw new RuntimeException(e);
        }
      } else {
        String message =
            teamJoinPayload.getEvent().getText() + " check for " + "@Brobot My git name is |"
                + teamJoinPayload.getEvent().getText().contains("@Brobot My git name is ");
        try {
          slackService.sendPrivateMessage("roman",
              message);
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
