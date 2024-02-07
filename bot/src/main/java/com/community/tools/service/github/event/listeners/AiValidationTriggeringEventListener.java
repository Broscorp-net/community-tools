package com.community.tools.service.github.event.listeners;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.github.PullRequestValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiValidationTriggeringEventListener implements
    TaskStatusChangeEventListener {

  private final PullRequestValidationService pullRequestValidationService;
  @Value("${github.org.name}")
  private String githubOrgName;

  @Override
  public void handleEvent(TaskStatusChangeEventDto event) {
    if (event.getTaskStatus().equals(TaskStatus.READY_FOR_REVIEW)) {
      pullRequestValidationService.validatePullRequest(githubOrgName + "/"
          + event.getTaskName() + "-" + event.getTraineeGitName());
    }
  }
}
