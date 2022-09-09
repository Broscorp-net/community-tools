package com.community.tools.dto;

import com.community.tools.model.TaskNameAndStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class UserForTaskStatusDto {

  private final String gitName;
  //TODO fill this field
  private LocalDate dateRegistration = null;
  private final LocalDate dateLastActivity;
  private final Integer completedTasks;
  private final List<TaskNameAndStatus> taskStatuses;


  public UserForTaskStatusDto(String gitName, LocalDate dateLastActivity, Integer completedTasks,
      List<TaskNameAndStatus> taskStatuses) {
    this.gitName = gitName;
    this.dateLastActivity = dateLastActivity;
    this.completedTasks = completedTasks;
    this.taskStatuses = taskStatuses;
  }

}
