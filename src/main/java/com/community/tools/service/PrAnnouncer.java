package com.community.tools.service;


import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PrAnnouncer {

  @Value("${token}")
  private String ghToken;

  @Value("${repository}")
  private String ghRepository;

  @Value("${slackWebHook}")
  private String slackWebHook;

  @Scheduled(cron = "0 0 * * * ?")
  public void prAnnouncement() throws IOException {
    long currentTime = System.currentTimeMillis();
    List<GHEventInfo> events = getGitHub().getEvents();
    for (GHEventInfo event : events) {
      long eventTime = event.getCreatedAt().getTime();
      if (timeComparing(currentTime, eventTime)) {
        String eventName = event.getType().name();
        if (eventName.equalsIgnoreCase("ready for review")) {
          sendAnnouncement(event.getActor().getName() + " " + event.getRepository().getName());
        }
      }
    }
    GHRepository repository = getGitHub().getRepository(ghRepository);
    List<GHPullRequest> pullList = repository.getPullRequests(GHIssueState.OPEN);
    for (GHPullRequest pullReq : pullList) {
      long eventTime = pullReq.getCreatedAt().getTime();
      if (timeComparing(currentTime, eventTime)) {
        String prDescription = pullReq.getBody();
        sendAnnouncement(prDescription);
      }
    }
  }

  private GitHub getGitHub() throws IOException {
    GitHub gitHub = new GitHubBuilder()
        .withOAuthToken(ghToken).build();
    return gitHub;
  }

  public void sendAnnouncement(String message) throws IOException {
    String url = slackWebHook;
    Payload payload = Payload.builder().text(message).build();
    Slack slack = Slack.getInstance();
    slack.send(url, payload);
  }

  public boolean timeComparing(long currentTime, long eventTime) {
    Calendar currentTimeCal = Calendar.getInstance();
    Calendar eventTimeCal = Calendar.getInstance();
    currentTimeCal.setTimeInMillis(currentTime);
    eventTimeCal.setTimeInMillis(eventTime);
    int currentTimeHour = currentTimeCal.get(Calendar.HOUR_OF_DAY);
    int eventTimeHour = eventTimeCal.get(Calendar.HOUR_OF_DAY);
    return currentTimeHour == eventTimeHour;
  }
}


