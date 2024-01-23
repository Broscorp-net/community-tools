package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskReviewEventDto;
import com.community.tools.service.github.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskReviewEventHandler implements EventHandler<TaskReviewEventDto> {

  @Override
  public void handleEvent(TaskReviewEventDto eventJson) {
    log.info("I am invoked");
  }
}
