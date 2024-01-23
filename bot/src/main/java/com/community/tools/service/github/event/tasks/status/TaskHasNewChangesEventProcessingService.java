package com.community.tools.service.github.event.tasks.status;

import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import com.community.tools.service.github.event.tasks.status.handlers.task.TaskHasNewChangesEventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskHasNewChangesEventProcessingService extends
    AbstractEventProcessingService<TaskHasNewChangesEventHandler> {

  public TaskHasNewChangesEventProcessingService(
      List<EventHandler<TaskHasNewChangesEventHandler>> eventHandlers) {
    super(eventHandlers);
  }
}
