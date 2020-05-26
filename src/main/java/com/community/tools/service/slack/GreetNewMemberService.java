package com.community.tools.service.slack;

import com.community.tools.service.github.GitHubService;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.app_backend.events.EventsDispatcher;

import com.github.seratch.jslack.app_backend.events.handler.AppMentionHandler;
import com.github.seratch.jslack.app_backend.events.payload.AppMentionPayload;
import com.github.seratch.jslack.app_backend.events.handler.TeamJoinHandler;

import com.github.seratch.jslack.app_backend.events.handler.AppHomeOpenedHandler;
import com.github.seratch.jslack.app_backend.events.payload.AppHomeOpenedPayload;
import com.github.seratch.jslack.app_backend.events.handler.MessageMeHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessageMePayload;

import com.github.seratch.jslack.app_backend.events.payload.TeamJoinPayload;
import com.github.seratch.jslack.app_backend.events.handler.MessageBotHandler;
import com.github.seratch.jslack.app_backend.events.payload.MessageBotPayload;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GreetNewMemberService {

  @Value("${DB_URL}")
  private String dbUrl;
  @Value("${DB_USER_NAME}")
  private String username;
  @Value("${DB_PASSWORD}")
  private String password;

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
  private MessageMeHandler messageHandler = new MessageMeHandler() {
    @Override
    public void handle(MessageMePayload messagePayload) {

      try {
        slackService.sendPrivateMessage("roman",
            "Ladies and gentleman, we got them, username: " + messagePayload.getEvent()
                .getUsername());
        slackService.sendPrivateMessage(messagePayload.getEvent().getUsername(),
            "Welcome to the club buddy :dealwithit:");
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  };
  private AppHomeOpenedHandler appHomeOpenedHandler = new AppHomeOpenedHandler() {
    @Override
    public void handle(AppHomeOpenedPayload appHomeOpenedPayload) {

      try {
        slackService.sendPrivateMessage("roman",
            "Ladies and gentleman, we got them. APPUSERHENDLER user: " +
                appHomeOpenedPayload.getEvent().getUser());
        slackService.sendPrivateMessage(appHomeOpenedPayload.getEvent().getUser(),
            "Welcome to the club buddy :dealwithit:");
      } catch (IOException | SlackApiException e) {
        throw new RuntimeException(e);
      }
    }
  };
  private AppMentionHandler appMentionHandler = new AppMentionHandler() {
    @Override
    public void handle(AppMentionPayload teamJoinPayload) {
      String check = "My git name is ";
      if (teamJoinPayload.getEvent().getText().contains(check)) {
        String message = teamJoinPayload.getEvent().getText();
        int p = message.indexOf(check);
        message = message.substring(p + check.length());
        String nick = message;
        try {
          slackService.sendPrivateMessage("roman",
              "ok i'll check your nick " + nick);
        } catch (IOException | SlackApiException e) {
          throw new RuntimeException(e);
        }
        List<String> list = new LinkedList<>();
        gitHubService.getGitHubAllUsers().stream().filter(user -> user.getLogin().equals(nick))
            .forEach(users -> {
              list.add("workaround");
            });
        if (list.size() > 0) {
          SingleConnectionDataSource connect = new SingleConnectionDataSource();
          connect.setDriverClassName("org.postgresql.Driver");
          connect.setUrl(dbUrl);
          connect.setUsername(username);
          connect.setPassword(password);
          JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
          jdbcTemplate.update("UPDATE public.state_entity SET  git_name= '" + nick + "'"
              + "\tWHERE userid='" + teamJoinPayload.getEvent().getUser() + "';");
          try {
            slackService.sendPrivateMessage("roman",
                "congrats your nick available " + teamJoinPayload.getEvent().getUser());
          } catch (IOException | SlackApiException e) {
            e.printStackTrace();
          }
        } else {
          try {
            slackService.sendPrivateMessage("roman",
                "Sry but looks like you are still not added to our team :worried:");
          } catch (IOException | SlackApiException e) {
            e.printStackTrace();
          }
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
      dispatcher.register(messageHandler);
      dispatcher.register(appHomeOpenedHandler);

    }
  }

  @Bean
  public ServletRegistrationBean<GreatNewMemberServlet> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new GreatNewMemberServlet(), "/greatNewMember/*");
  }
}
