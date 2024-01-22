package com.community.tools.service;

import com.community.tools.discord.DiscordService;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {


  private static final String NOT_FOUND_DISCORD_USER_MESSAGE = "User with this discord name not found";

  @Value("${text.channel}")
  private String textChannelName;
  @Autowired
  private DiscordService discordService;
  @Autowired
  private DiscordGithubService discordGithubService;

  /**
   * Sends a pull request (PR) update notification to the Discord user associated with the given
   * GitHub username.
   *
   * @param gitHubName            The GitHub username for which the PR update notification is
   *                              intended.
   * @param taskNameAndStatusList A list containing TaskNameAndStatus objects representing the
   *                              updated task statuses.
   * @throws IllegalArgumentException If the Discord user corresponding to the GitHub username is
   *                                  not found.
   */
  public void sendPRUpdateNotification(String gitHubName,
      List<TaskNameAndStatus> taskNameAndStatusList) {
    String discordName = discordGithubService.getDiscordName(gitHubName);
    Optional<User> userOptional = discordService.getUserByName(discordName);
    if (userOptional.isPresent()) {
      discordService.sendMessageToConversation(textChannelName,
          buildNotification(userOptional.get(), taskNameAndStatusList));
    } else {
      throw new IllegalArgumentException(NOT_FOUND_DISCORD_USER_MESSAGE);
    }
  }

  /**
   * Sends pull request (PR) update notifications to Discord users based on a list of
   * UserForTaskStatusDto objects.
   *
   * @param userForTaskStatusDtoList A list of UserForTaskStatusDto objects representing GitHub
   *                                 usernames and their associated task statuses.
   * @throws IllegalArgumentException If any Discord user corresponding to the GitHub usernames is
   *                                  not found.
   */
  public void sendPRUpdateNotification(List<UserForTaskStatusDto> userForTaskStatusDtoList) {
    List<String> gitHubNames = userForTaskStatusDtoList.stream()
        .map(UserForTaskStatusDto::getGitName).collect(
            Collectors.toList());
    Map<String, String> gitHubDiscordNames = discordGithubService.getDiscordGithubUsernames(
        gitHubNames)
    for (UserForTaskStatusDto dto : userForTaskStatusDtoList) {
      String discordName = gitHubDiscordNames.get(dto.getGitName());
      Optional<User> userOptional = discordService.getUserByName(discordName);
      if (userOptional.isPresent()) {
        discordService.sendMessageToConversation(textChannelName,
            buildNotification(userOptional.get(), dto.getTaskStatuses()));
      } else {
        throw new IllegalArgumentException(NOT_FOUND_DISCORD_USER_MESSAGE);
      }
    }
  }

  /**
   * Sends a general notification message to the Discord user associated with the given GitHub
   * username.
   *
   * @param gitHubName The GitHub username for which the notification message is intended.
   * @param message    The message content to be sent to the Discord user.
   * @throws IllegalArgumentException If the Discord user corresponding to the GitHub username is
   *                                  not found.
   */
  public void sendNotificationMessage(String gitHubName, String message) {
    String discordName = discordGithubService.getDiscordName(gitHubName);
    Optional<User> userOptional = discordService.getUserByName(discordName);
    if (userOptional.isPresent()) {
      discordService.sendMessageToConversation(textChannelName,
          userOptional.get().getAsMention() + ", " + message);
    } else {
      throw new IllegalArgumentException(NOT_FOUND_DISCORD_USER_MESSAGE);
    }
  }

  /**
   * Builds a notification message for a Discord user based on their updated task statuses.
   *
   * @param user                  The Discord user for whom the notification message is being
   *                              built.
   * @param taskNameAndStatusList A list containing TaskNameAndStatus objects representing the
   *                              updated task statuses.
   * @return The built notification message as a String.
   */
  private String buildNotification(User user, List<TaskNameAndStatus> taskNameAndStatusList) {
    StringBuilder announcementText = new StringBuilder();
    announcementText.append("Hey ").append(user.getAsMention())
        .append("! Your PR statuses updated on: ");
    for (TaskNameAndStatus taskNameAndStatus : taskNameAndStatusList) {
      announcementText.append(System.lineSeparator());
      announcementText.append(taskNameAndStatus.getTaskName()).append(" :")
          .append(taskNameAndStatus.getTaskStatus());
    }
    return announcementText.toString();
  }

}
