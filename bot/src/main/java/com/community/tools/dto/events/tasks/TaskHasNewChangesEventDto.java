package com.community.tools.dto.events.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskHasNewChangesEventDto {

  private String taskName;
  private String traineeGitName;
  private String pullUrl;
}
