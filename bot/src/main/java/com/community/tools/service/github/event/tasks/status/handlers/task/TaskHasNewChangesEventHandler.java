package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskHasNewChangesEventDto;
import com.community.tools.service.github.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskHasNewChangesEventHandler implements EventHandler<TaskHasNewChangesEventDto> {

  @Override
  public void handleEvent(TaskHasNewChangesEventDto eventJson) {
    log.info("I am invoked");
  }
}
