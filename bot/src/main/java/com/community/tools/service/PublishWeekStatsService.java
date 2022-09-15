package com.community.tools.service;

import com.community.tools.model.EventData;
import com.community.tools.model.Messages;
import com.community.tools.service.github.GitHubService;
import com.github.seratch.jslack.api.methods.SlackApiException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Must be refactored using new ClassroomService.
 */
@Component
@RequiredArgsConstructor
public class PublishWeekStatsService {

  @Autowired
  private GitHubService ghEventService;

  @Value("${importantInformationChannel}")
  private String channel;

  @Value("${urlServer}")
  private String urlServer;

  @Autowired
  private MessageService messageService;

  @Autowired
  private MessageConstructor messageConstructor;

  /**
   * Publish statistics of Events for last week. Statistic sends every Monday.
   *
   * @throws SlackApiException SlackApiException
   * @throws IOException       IOException
   */
  //@Scheduled(cron = "0 0 0 * * MON")
  public void exportStat()
      throws SlackApiException, IOException {
    Date endDate = new Date();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -7);
    Date startDate = cal.getTime();

    List<EventData> events = ghEventService.getEvents(startDate, endDate);
    if (events.size() == 0) {
      messageService.sendMessageToConversation(channel, Messages.NO_ACTIVITY_MESSAGE);
      System.out.println(events);
    } else {
      messageService.sendBlockMessageToConversation(channel,
          messageConstructor.createStatisticMessage(
              events));
    }
  }

  /**
   * Publish message with link to trainee`s leaderboard and image (first 5 record of rating).
   *
   * @throws IOException       IOException
   * @throws SlackApiException SlackApiException
   */
//  @Scheduled(cron = "0 10 0 * * MON")
  public void publishLeaderboard() throws IOException, SlackApiException {
    String url = urlServer + "leaderboard/";
    String date = LocalDate.now().toString();
    String img = url + "img/" + date;

    messageService.sendBlockMessageToConversation(channel,
        messageConstructor.createInfoLinkMessage(Messages.RATING_MESSAGE, url, img));
  }

  /**
   * Publish message with link to trainee`s tasks status and image (first 5 record of rating).
   */
//  @Scheduled(cron = "0 20 0 * * ?")
  public void publishTasksStatus() {
    String url = urlServer + "task-status";
    String date = LocalDate.now().toString();
    String img = url + "img/" + date;

    messageService.sendBlockMessageToConversation(channel,
        messageConstructor.createInfoLinkMessage(Messages.TASKS_STATUS_MESSAGE, url, img));
  }
}