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
public class TaskIsDoneMentorNotifyingEventHandler implements EventHandler<TaskStatusEventDto> {

  private final MentorNotificationService mentorNotificationService;

  @Override
  public void handleEvent(TaskStatusEventDto eventDto) {
    if (eventDto.getTaskStatus()
        .equals(TaskStatus.DONE)) {
      mentorNotificationService.notifyAllTraineeMentors(eventDto.getTraineeGitName(),
          formNotificationString(eventDto));
    }
  }

  private String formNotificationString(TaskStatusEventDto eventDto) {
    return "Task " + eventDto.getTaskName() + " by " + eventDto.getTraineeGitName() + " at "
        + eventDto.getPullUrl() + " is marked Done now!";
  }
}
