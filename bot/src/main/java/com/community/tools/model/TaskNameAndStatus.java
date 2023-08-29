package com.community.tools.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskNameAndStatus {
  private final String taskName;
  private final String pullUrl;
  private final String taskStatus;

}
