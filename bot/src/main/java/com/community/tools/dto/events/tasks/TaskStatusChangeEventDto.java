package com.community.tools.dto.events.tasks;

import com.community.tools.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TaskStatusChangeEventDto {

  @NonNull
  private String taskName;
  @NonNull
  private String traineeGitName;
  @NonNull
  private String pullUrl;
  private boolean withNewChanges;
  @NonNull
  private TaskStatus taskStatus;
  private String reviewerGitName;
}
