package com.community.tools.service.github.event.tasks.status;

import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskIsDoneEventProcessingService extends
    AbstractEventProcessingService<TaskIsDoneEventProcessingService> {

  public TaskIsDoneEventProcessingService(
      List<EventHandler<TaskIsDoneEventProcessingService>> eventHandlers) {
    super(eventHandlers);
  }
}
