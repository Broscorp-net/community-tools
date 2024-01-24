package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskStatusEventDto;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.MentorNotificationService;
import com.community.tools.service.github.event.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReadyForReviewMentorNotifyingEventHandler implements
    EventHandler<TaskStatusEventDto> {

  private final MentorNotificationService mentorNotificationService;
  private static final String NOTIFICATION_TEMPLATE = "Task %s by %s at %s is Ready For Review!";

  @Override
  public void handleEvent(TaskStatusEventDto eventDto) {
    if (!eventDto.isWithNewChanges() && eventDto.getTaskStatus()
        .equals(TaskStatus.READY_FOR_REVIEW)) {
      mentorNotificationService.notifyAllTraineeMentors(eventDto.getTraineeGitName(),
          String.format(NOTIFICATION_TEMPLATE, eventDto.getTaskName(),
              eventDto.getTraineeGitName(),
              eventDto.getPullUrl()));
    }
  }
}