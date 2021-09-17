package com.community.tools.dto;

import com.community.tools.model.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class UserTasksStatusDto {

  private String loginPlatform;

  private String loginGithub;

  private Integer completedTasks;

  private Map<String, String> taskStatusMap;

  /**
   * Converted user to user dto.
   * @param user user
   * @param tasksForUsers array tasks
   * @return user dto
   */
  public static UserTasksStatusDto fromUser(User user, String[] tasksForUsers) {
    UserTasksStatusDto userTasksStatusDto = new UserTasksStatusDto();
    userTasksStatusDto.setLoginPlatform(user.getPlatformName());
    userTasksStatusDto.setLoginGithub(user.getGitName());
    userTasksStatusDto.setCompletedTasks(user.getCompletedTasks());
    userTasksStatusDto.setTaskStatusMap(new HashMap<>());

    Arrays.stream(tasksForUsers).forEach(task -> {
      user.getTaskStatuses().forEach(taskStatus -> {
        if (task.equals(taskStatus.getTaskName())) {
          userTasksStatusDto.getTaskStatusMap().put(task, taskStatus.getTaskStatus());
        }
      });
    });
    return userTasksStatusDto;
  }
}
