package com.community.tools.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.discord.DiscordService;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.service.github.DiscordGithubService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private DiscordService discordService;

  @Mock
  private DiscordGithubService discordGithubService;

  @InjectMocks
  private NotificationService notificationService;

  private final String gitHubName = "exampleGitHubUser";
  private final String discordName = "exampleDiscordUser";

  @Test
  void shouldSentMessageToConversationWhenSendPRUpdateNotification() {
    when(discordGithubService.getDiscordName(gitHubName)).thenReturn(discordName);
    when(discordService.getUserByName(discordName)).thenReturn(Optional.of(mock(User.class)));

    notificationService.sendPRUpdateNotification(gitHubName, Collections.emptyList());

    verify(discordService).sendMessageToConversation(isNull(), any(String.class));
  }

  @Test
  void shouldThrowNotificationUserNotFoundExceptionWhenUserNotFoundByDiscordName() {
    when(discordGithubService.getDiscordName(gitHubName)).thenReturn(discordName);
    when(discordService.getUserByName(discordName)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      notificationService.sendPRUpdateNotification(gitHubName, Collections.emptyList());
    });
  }

  @Test
  void shouldSentMessageToConversationWhenSendPRUpdateNotificationList() {
    UserForTaskStatusDto userDto = new UserForTaskStatusDto(gitHubName, LocalDate.now(), 1,
        Collections.singletonList(new TaskNameAndStatus("Task1", "PullUrl1", "Status1")));

    when(discordGithubService.getDiscordGithubUsernames(anyList())).thenReturn(
        Collections.singletonMap(gitHubName, discordName));
    when(discordService.getUserByName(discordName)).thenReturn(Optional.of(mock(User.class)));

    notificationService.sendPRUpdateNotification(Collections.singletonList(userDto));

    verify(discordService).sendMessageToConversation(isNull(), any(String.class));
  }

  @Test
  void shouldThrowNotificationUserNotFoundExceptionWhenSendPRUpdateNotificationListUserNotFound() {
    UserForTaskStatusDto userDto = new UserForTaskStatusDto(gitHubName, LocalDate.now(), 1,
        Collections.singletonList(new TaskNameAndStatus("Task1", "PullUrl1", "Status1")));

    when(discordGithubService.getDiscordGithubUsernames(anyList())).thenReturn(
        Collections.singletonMap(gitHubName, discordName));
    when(discordService.getUserByName(discordName)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      notificationService.sendPRUpdateNotification(Collections.singletonList(userDto));
    });
  }

  @Test
  void shouldSentMessageToConversationWhenSendNotificationMessage() {

    when(discordGithubService.getDiscordName(gitHubName)).thenReturn(discordName);
    when(discordService.getUserByName(discordName)).thenReturn(Optional.of(mock(User.class)));

    notificationService.sendNotificationMessage(gitHubName, "Test message");

    verify(discordService).sendMessageToConversation(isNull(), any(String.class));
  }

  @Test
  void shouldThrowNotificationUserNotFoundExceptionWhenSendNotificationMessageNotFoundUser() {
    when(discordGithubService.getDiscordName(gitHubName)).thenReturn(discordName);
    when(discordService.getUserByName(discordName)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      notificationService.sendNotificationMessage(gitHubName, "Test message");
    });
  }


}