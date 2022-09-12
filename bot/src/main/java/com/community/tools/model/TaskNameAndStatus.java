package com.community.tools.model;

import lombok.Data;

@Data
public class TaskNameAndStatus {

  private final String taskName;
  private final String taskStatus;

  public TaskNameAndStatus(String taskName, String taskStatus) {
    this.taskName = taskName;
    this.taskStatus = taskStatus;
  }

}
