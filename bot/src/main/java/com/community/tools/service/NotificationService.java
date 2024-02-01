package com.community.tools.service;

import com.community.tools.discord.DiscordService;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.service.github.DiscordGitHubMappingService;
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

  private static final String NOT_FOUND_DISCORD_USER_MESSAGE = "User with this discord name"
      + " not found: ";

  @Value("${text.channel}")
  private String textChannelName;
  private final DiscordService discordService;
  private final DiscordGitHubMappingService discordGitHubMappingService;

  public NotificationService(DiscordService discordService,
      DiscordGitHubMappingService discordGitHubMappingService) {
    this.discordService = discordService;
    this.discordGitHubMappingService = discordGitHubMappingService;
  }

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
  public void sendPullRequestUpdateNotification(String gitHubName,
      List<TaskNameAndStatus> taskNameAndStatusList) {
    String discordName = discordGitHubMappingService.getDiscordName(gitHubName);
    Optional<User> userOptional = discordService.getUserByName(discordName);
    checkUserAndSendNotification(userOptional, discordName, taskNameAndStatusList);
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
  public void sendPullRequestUpdateNotification(
      List<UserForTaskStatusDto> userForTaskStatusDtoList) {
    List<String> gitHubNames = userForTaskStatusDtoList.stream()
        .map(UserForTaskStatusDto::gitName).collect(
            Collectors.toList());
    Map<String, String> gitHubDiscordNames = discordGitHubMappingService.getDiscordGithubUsernames(
        gitHubNames);
    for (UserForTaskStatusDto dto : userForTaskStatusDtoList) {
      String discordName = gitHubDiscordNames.get(dto.gitName());
      Optional<User> userOptional = discordService.getUserByName(discordName);
      checkUserAndSendNotification(userOptional, discordName, dto.taskStatuses());
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
    String discordName = discordGitHubMappingService.getDiscordName(gitHubName);
    Optional<User> userOptional = discordService.getUserByName(discordName);
    if (userOptional.isPresent()) {
      discordService.sendMessageToConversation(textChannelName,
          userOptional.get().getAsMention() + ", " + message);
    } else {
      throw new IllegalArgumentException(NOT_FOUND_DISCORD_USER_MESSAGE + discordName);
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
      announcementText.append(taskNameAndStatus.taskName()).append(" :")
          .append(taskNameAndStatus.taskStatus());
    }
    return announcementText.toString();
  }

  /**
   * Checks if a Discord user is present and sends a notification.
   *
   * @param userOptional          An Optional of User representing the Discord user to check.
   * @param discordName           The Discord username associated with the GitHub account.
   * @param taskNameAndStatusList A list containing TaskNameAndStatus objects representing the
   *                              updated task statuses.
   * @throws IllegalArgumentException If the Discord user corresponding to the GitHub username is
   *                                  not found.
   */
  private void checkUserAndSendNotification(Optional<User> userOptional, String discordName,
      List<TaskNameAndStatus> taskNameAndStatusList) {
    if (userOptional.isPresent()) {
      discordService.sendMessageToConversation(textChannelName,
          buildNotification(userOptional.get(), taskNameAndStatusList));
    } else {
      throw new IllegalArgumentException(NOT_FOUND_DISCORD_USER_MESSAGE + discordName);
    }
  }

}
