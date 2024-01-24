package com.community.tools.dto;

import com.community.tools.model.TaskNameAndStatus;
import java.time.LocalDate;
import java.util.List;

public record UserForTaskStatusDto(
        String gitName,
        LocalDate dateLastActivity,
        Integer completedTasks,
        List<TaskNameAndStatus> taskStatuses) {

  /**
   * Constructor for DTO.
   *
   * @param gitName          - gitName
   * @param dateLastActivity - dateLastActivity
   * @param completedTasks   - completedTasks
   * @param taskStatuses     - list of task and status
   */
  public UserForTaskStatusDto {
  }
}
