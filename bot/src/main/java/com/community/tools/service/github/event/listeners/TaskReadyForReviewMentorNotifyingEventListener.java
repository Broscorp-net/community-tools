package com.community.tools.service.github.event.listeners;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.MentorNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReadyForReviewMentorNotifyingEventListener implements
    TaskStatusChangeEventListener {

  private final MentorNotificationService mentorNotificationService;
  private static final String NOTIFICATION_TEMPLATE = "Task %s by %s at %s is Ready For Review!";

  @Override
  public void handleEvent(TaskStatusChangeEventDto eventDto) {
    if (!eventDto.isWithNewChanges() && eventDto.getTaskStatus()
        .equals(TaskStatus.READY_FOR_REVIEW)) {
      mentorNotificationService.notifyAllTraineeMentors(eventDto.getTraineeGitName(),
          String.format(NOTIFICATION_TEMPLATE, eventDto.getTaskName(),
              eventDto.getTraineeGitName(),
              eventDto.getPullUrl()));
    }
  }
}
