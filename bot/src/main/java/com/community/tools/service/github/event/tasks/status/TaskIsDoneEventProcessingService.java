package com.community.tools.service.github.event.tasks.status;

import com.community.tools.dto.events.tasks.TaskIsDoneEventDto;
import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskIsDoneEventProcessingService extends
    AbstractEventProcessingService<TaskIsDoneEventDto> {

  public TaskIsDoneEventProcessingService(
      List<EventHandler<TaskIsDoneEventDto>> eventHandlers) {
    super(eventHandlers);
  }
}
