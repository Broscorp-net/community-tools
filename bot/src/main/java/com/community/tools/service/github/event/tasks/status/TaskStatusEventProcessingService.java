package com.community.tools.service.github.event.tasks.status;

import com.community.tools.dto.events.tasks.TaskStatusEventDto;
import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusEventProcessingService extends
    AbstractEventProcessingService<TaskStatusEventDto> {

  public TaskStatusEventProcessingService(
      List<EventHandler<TaskStatusEventDto>> eventHandlers) {
    super(eventHandlers);
  }
}
