package com.community.tools.service.github.event.listeners;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.github.PullRequestValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This event listener triggers AI validation upon the task first becoming READY_FOR_REVIEW.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiValidationTriggeringEventListener implements
    TaskStatusChangeEventListener {

  private final PullRequestValidationService pullRequestValidationService;
  @Value("${github.org.name}")
  private String githubOrgName;

  @Override
  public void handleEvent(TaskStatusChangeEventDto event) {
    if (event.getTaskStatus().equals(TaskStatus.READY_FOR_REVIEW)
        && !event.isWithNewChanges()) {
      log.info("Triggering ai validation for task " + event.getTaskName() + " by "
          + event.getTraineeGitName());
      pullRequestValidationService.validatePullRequest(githubOrgName + "/"
          + event.getTaskName() + "-" + event.getTraineeGitName(), event.getTraineeGitName());
    }
  }
}
