package com.community.tools.service;


import com.community.tools.service.github.GitHubConnectService;
import com.community.tools.service.slack.SlackService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("PRAnnouncer")

public class NewPRAnnouncer {

  private final GitHubConnectService service;
  private final SlackService slackService;

  @Scheduled(cron = "0 0 * * * ?")
  public void prAnnouncement() {

    List<GHEventInfo> events;
    try {
      events = service.getGitHubConnection().getEvents();
      for (GHEventInfo event : events) {
        long eventOccuranceTime = event.getCreatedAt().getTime();
        int eventDayofYear = LocalDate.ofEpochDay(eventOccuranceTime).getDayOfYear();
        int timeofEvent = LocalTime.ofSecondOfDay(eventOccuranceTime).getHour();
        int presentDayofYear = LocalDateTime.now().getDayOfYear();
        int presentTime = LocalDateTime.now().getHour();
        if (eventDayofYear == presentDayofYear && timeofEvent == presentTime) {
          String eventName = event.getType().name();
          if (eventName.equalsIgnoreCase("ready for review")) {
            slackService.sendAnnouncement(
                event.getActor().getName() + " " + event.getRepository().getName());
          }
        }
      }
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullList = repository.getPullRequests(GHIssueState.OPEN);
      for (GHPullRequest pullReq : pullList) {
        long pullReqTime = pullReq.getCreatedAt().getTime();
        int pullReqDayofYear = LocalDate.ofEpochDay(pullReqTime).getDayOfYear();
        int timeofPullReq = LocalTime.ofSecondOfDay(pullReqTime).getHour();
        int presentDayofYear = LocalDateTime.now().getDayOfYear();
        int presentTime = LocalDateTime.now().getHour();
        if (pullReqDayofYear == presentDayofYear && timeofPullReq == presentTime) {
          String prDescription = pullReq.getBody();
          slackService.sendAnnouncement(prDescription);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
