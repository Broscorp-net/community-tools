package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskIsDoneEventDto;
import com.community.tools.service.github.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskIsDoneEventHandler implements EventHandler<TaskIsDoneEventDto> {

  @Override
  public void handleEvent(TaskIsDoneEventDto eventJson) {
    log.info("I am invoked");
  }
}
