package com.community.tools.service.github.event.tasks.status;

import com.community.tools.dto.events.tasks.TaskHasNewChangesEventDto;
import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import com.community.tools.service.github.event.tasks.status.handlers.task.TaskHasNewChangesEventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskHasNewChangesEventProcessingService extends
    AbstractEventProcessingService<TaskHasNewChangesEventDto> {

  public TaskHasNewChangesEventProcessingService(
      List<EventHandler<TaskHasNewChangesEventDto>> eventHandlers) {
    super(eventHandlers);
  }
}
