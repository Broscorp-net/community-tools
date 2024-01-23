package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskReadyForReviewEventDto;
import com.community.tools.service.github.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskReadyForReviewEventHandler implements EventHandler<TaskReadyForReviewEventDto> {

  @Override
  public void handleEvent(TaskReadyForReviewEventDto eventJson) {
    log.info("I am invoked");
  }
}
