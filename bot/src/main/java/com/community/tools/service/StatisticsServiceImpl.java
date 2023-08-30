package com.community.tools.service;

import com.community.tools.discord.DiscordService;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForTaskStatusDto;
import com.community.tools.model.TaskNameAndStatus;
import com.community.tools.model.TaskStatus;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticService {

  @Value("${defaultNumberOfDaysForStatistic}")
  private Integer defaultNumberOfDays;
  @Value("${defaultRowLimit}")
  private Integer defaultUserLimit;
  @Value("${tasksForUsers}")
  private String originalTaskNames;
  @Value("${hallOfFameChannel}")
  private String hallOfFameChannel;
  private final Map<String, Comparator<GithubUserDto>> comparators
          = new HashMap<>();
  private final String[] tableHeadTasks = {"Intro", "Generics", "GameOfLife", "GC"};
  @Autowired
  private DiscordService discordService;
  @Autowired
  private TaskStatusService taskStatusService;


  /**
   * Generates and sends daily statistics to the Discord channel using the provided data.
   * The statistics include information about completed tasks and their status.
   *
   * @Scheduled annotation specifies the schedule for executing this method.
   */
  @Scheduled(cron = "0 0 12 * * ?", zone = "Europe/Kiev")
  public void createStatisticsForDiscord() {
    List<UserForTaskStatusDto> statisticsList = getStatisticsList();
    int firstColLength = getMaxColLength(statisticsList);
    int userRowsLimit = 3;
    String[] taskNames = originalTaskNames.split(", ");

    createStatisticsTitle();

    sendTextMessageToDiscord(createMonoText(createTableHead(firstColLength)));

    for (int i = 0; i < statisticsList.size(); i += userRowsLimit) {
      int toIndex = Math.min(i + userRowsLimit, statisticsList.size());

      sendTextMessageToDiscord(createMonoText(createTableBody(statisticsList.subList(i, toIndex),
              firstColLength, taskNames)));
    }
  }

  private void createStatisticsTitle() {
    String tableTitle = "Daily statistics for "
            + getCurrentFormattedDate("Europe/Kiev");
    sendTextMessageToDiscord(createH2Text(tableTitle));
  }

  private String createTableLegend() {
    return "\n`:white_check_mark:` - DONE, `:red_square:` - DENIED, "
            + "`:yellow_square:` - IN PROGRESS, `:white_large_square:` - UNDEFINED\n\n\n\n";
  }

  private List<UserForTaskStatusDto> getStatisticsList() {
    Comparator<GithubUserDto> comparator = comparators.getOrDefault(
            "DESC".toUpperCase(),
            Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed());

    return taskStatusService.getTaskStatuses(
            Period.ofDays(defaultNumberOfDays),
            defaultUserLimit,
            comparator);
  }

  private int getMaxColLength(List<UserForTaskStatusDto> userStatusDtoList) {
    return userStatusDtoList.stream()
            .mapToInt(userStatusDto -> {
              String userName = userStatusDto.getGitName();
              return userName != null ? userName.length() : 0;
            })
            .max()
            .orElse(0);
  }

  private String createTableHead(int firstColLength) {
    StringBuilder tableHead = new StringBuilder();

    String headFirstCol = "Name";

    tableHead.append(createTableLegend());


    tableHead.append(headFirstCol)
            .append(StringUtils.repeat(" ",
                    firstColLength - headFirstCol.length()))
            .append("  ")
            .append(getVerticalSeparator());

    for (int i = 0; i < tableHeadTasks.length; i++) {
      tableHead.append(tableHeadTasks[i])
              .append("  ");
      if (i != tableHeadTasks.length - 1) {
        tableHead.append(getVerticalSeparator());
      } else {
        tableHead.append(getLastVerticalSeparator());
      }
    }
    tableHead.append(getHorizontalSeparator(firstColLength));

    return tableHead.toString();
  }

  private String createTableBody(List<UserForTaskStatusDto> userPartList,
                                 int firstColLength, String[] taskNames) {
    StringBuilder tableBody = new StringBuilder();

    for (UserForTaskStatusDto userData : userPartList) {
      String userName = userData.getGitName();
      tableBody.append(userName)
              .append(createDiscordLink(createGitHubLink(userName)))
              .append(StringUtils.repeat(" ", firstColLength - userName.length()))
              .append(getVerticalSeparator());

      for (int i = 0; i < taskNames.length; i++) {
        String label = getTaskSmileStatus(userData.getTaskStatuses(), taskNames[i]);
        String pullUrl = getTaskPullUrl(userData.getTaskStatuses(), taskNames[i]);

        if (!label.equals(TaskStatus.UNDEFINED.getEmoji()) && pullUrl != null) {
          tableBody.append(createEmojiDiscordLink(pullUrl, label));
        } else {
          tableBody.append("`" + label + "`").append(" ");
        }

        tableBody.append(StringUtils.repeat(" ",
                tableHeadTasks[i].length() - 3));

        if (i != taskNames.length - 1) {
          tableBody.append(getVerticalSeparator());
        } else {
          tableBody.append(getLastVerticalSeparator());
        }
      }
      tableBody.append(getHorizontalSeparator(firstColLength));
    }
    return tableBody.toString();
  }

  private String createEmojiDiscordLink(String url, String emoji) {
    return "`" + emoji + "[↗](<" + url + ">)`";
  }

  private String createDiscordLink(String url) {
    return "`[↗](<" + url + ">)`";
  }

  private String createGitHubLink(String userName) {
    return "https://github.com/" + userName;
  }

  private String getCurrentFormattedDate(String timeZoneId) {
    ZoneId zoneId = ZoneId.of(timeZoneId);
    LocalDate currentDate = LocalDate.now(zoneId);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return currentDate.format(dateFormatter);
  }

  private String getVerticalSeparator() {
    return "       |    ";
  }

  private String getLastVerticalSeparator() {
    return "             ";
  }

  private String getHorizontalSeparator(int firstColLength) {
    int separatorLen = getVerticalSeparator().length();
    int colWidth = firstColLength + separatorLen * 2;

    for (String taskName : tableHeadTasks) {
      colWidth += taskName.length();
      colWidth += separatorLen;
    }
    return "\n" + StringUtils.repeat("-", colWidth - 1) + "\n";
  }

  private void sendTextMessageToDiscord(String message) {
    discordService.sendMessageToConversation(hallOfFameChannel, message);
  }

  private String createMonoText(String message) {
    return "`" + message + "`";
  }

  private String createH2Text(String message) {
    return "## " + message;
  }

  private String getTaskPullUrl(List<TaskNameAndStatus> taskNameAndStatuses, String taskName) {
    for (TaskNameAndStatus task : taskNameAndStatuses) {
      if (taskName.equals(task.getTaskName())) {
        return task.getPullUrl();
      }
    }
    return null;
  }

  private String getTaskSmileStatus(List<TaskNameAndStatus> taskNameAndStatuses,
                                    String taskName) {
    String status = taskNameAndStatuses.stream()
            .filter(task -> taskName.equals(task.getTaskName()))
            .map(TaskNameAndStatus::getTaskStatus)
            .findFirst().orElse("undefined");

    return TaskStatus.getEmojiByDescription(status);
  }
}
