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
public class TaskIsDoneMentorNotifyingEventListener implements TaskStatusChangeEventListener {

  private final MentorNotificationService mentorNotificationService;
  private static final String NOTIFICATION_TEMPLATE = "Task %s by %s at %s is marked Done now!";

  @Override
  public void handleEvent(TaskStatusChangeEventDto eventDto) {
    if (eventDto.getTaskStatus()
        .equals(TaskStatus.DONE)) {
      mentorNotificationService.notifyAllTraineeMentors(eventDto.getTraineeGitName(),
          String.format(NOTIFICATION_TEMPLATE, eventDto.getTaskName(),
              eventDto.getTraineeGitName(),
              eventDto.getPullUrl()));
    }
  }
}
