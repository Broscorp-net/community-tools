package com.community.tools.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.community.tools.service.github.GitHubService;
import com.community.tools.service.slack.SlackService;
import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PublishWeekStatsServiceTest {

  @Test
  void exportStatTest_ChatNameTest() {
    List<EventData> events = new ArrayList<>();
    EventData evet = new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED);
    events.add(evet);

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
        slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendMessageToChat(eq("test_2"), anyString());
    });
  }

  @Test
  void exportStatTest() {
    String message = ":construction: ТИПЫ :construction:\n"
        + "COMMENT:loudspeaker: : 3\n"
        + "PULL_REQUEST_CREATED:mailbox_with_mail:: 2\n"
        + "PULL_REQUEST_CLOSED:moneybag:: 1\n"
        + " ----------------------------------------\n"
        + ":construction: АКТИВНОСТЬ :construction:\n"
        + "roman: :loudspeaker: :loudspeaker: :loudspeaker:"
        + " :mailbox_with_mail::mailbox_with_mail::moneybag:\n";
    List<EventData> events = Arrays.asList(
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT));

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
        slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendMessageToChat(anyString(), eq(message));
    });
  }


  @Test
  void exportStatTestTwoAuthors() {
    String message = ":construction: ТИПЫ :construction:\n"
        + "COMMENT:loudspeaker: : 4\n"
        + "PULL_REQUEST_CREATED:mailbox_with_mail:: 3\n"
        + "COMMIT:rolled_up_newspaper:: 2\n"
        + "PULL_REQUEST_CLOSED:moneybag:: 1\n"
        + " ----------------------------------------\n"
        + ":construction: АКТИВНОСТЬ :construction:\n"
        + "roman: :loudspeaker: :loudspeaker: :loudspeaker: :mailbox_with_mail::mailbox_with_mail::moneybag:\n"
        + "Ilona: :loudspeaker: :mailbox_with_mail::rolled_up_newspaper::rolled_up_newspaper:\n";
    List<EventData> events = Arrays.asList(
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CLOSED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "roman", Event.COMMENT),
        new EventData(new Date(), "Ilona", Event.COMMIT),
        new EventData(new Date(), "Ilona", Event.COMMIT),
        new EventData(new Date(), "Ilona", Event.PULL_REQUEST_CREATED),
        new EventData(new Date(), "Ilona", Event.COMMENT)
    );

    GitHubService gitHubEventService = mock(GitHubService.class);
    SlackService slackService = mock(SlackService.class);
    Mockito.when(gitHubEventService.getEvents(any(), any())).thenReturn(events);

    PublishWeekStatsService taskTestService = new PublishWeekStatsService(gitHubEventService,
        slackService);
    assertDoesNotThrow(() -> {
      taskTestService.exportStat();
      Mockito.verify(slackService).sendMessageToChat(anyString(), eq(message));
    });
  }
}
